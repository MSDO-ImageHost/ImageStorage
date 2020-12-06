package dk.sdu.imagehost.imagestorage.ampq

import com.beust.klaxon.Klaxon
import com.rabbitmq.client.*
import dk.sdu.imagehost.imagestorage.json.Base64Converter
import dk.sdu.imagehost.imagestorage.json.DateTimeConverter
import dk.sdu.imagehost.imagestorage.json.UUIDConverter
import java.io.Closeable
import java.lang.Exception
import java.net.URI

class AMQP(val uri: URI, val callback: EventCallback) : Closeable {

    val connection: Connection = ConnectionFactory().run {
        setUri(uri)
        newConnection()
    }

    val responseChannel = connection.createChannel().apply {
        exchangeDeclare("rapid", "direct")
        queueDeclare("ImageStorage", false, false, false, emptyMap())
    }

    val klaxon = Klaxon().converter(Base64Converter).converter(DateTimeConverter).converter(UUIDConverter)

    init {
        val listeningChannel = connection.createChannel().apply {
            exchangeDeclare("rapid", "direct")
            queueDeclare("ImageStorage", false, false, false, emptyMap())
        }
        listeningChannel.queueBind("ImageStorage", "rapid", "ImageCreateRequest")
        listeningChannel.queueBind("ImageStorage", "rapid", "ImageLoadRequest")
        listeningChannel.queueBind("ImageStorage", "rapid", "ImageDeleteRequest")

        listeningChannel.basicConsume("ImageStorage", true, DeliverCallback(::receive), CANCEL_NOOP)
    }

    override fun close() {
        connection.close()
    }

    private fun receive(consumerTag: String, delivery: Delivery) {
        val body = try {
            String(delivery.body)
        } catch (e: Exception) {
            return
        }
        println("Received message:\n\t${delivery.envelope.routingKey}\n\t$body")
        val event = when (delivery.envelope.routingKey) {
            "ImageLoadRequest" -> klaxon.parse<ImageStorageEvent.Request.Load>(body)!!
            "ImageCreateRequest" -> klaxon.parse<ImageStorageEvent.Request.Create>(body)!!
            "ImageDeleteRequest" -> klaxon.parse<ImageStorageEvent.Request.Delete>(body)!!
            else -> return
        }
        callback(event, ::send)
    }

    fun send(response: ImageStorageEvent.Response) {
        val json: String = klaxon.toJsonString(response)
        println("Sending message:\n\t${response.TAG}\n\t$json")
        responseChannel.basicPublish("rapid", response.TAG, null, json.toByteArray());
    }

    companion object {
        private val CANCEL_NOOP = CancelCallback { }
    }

}
