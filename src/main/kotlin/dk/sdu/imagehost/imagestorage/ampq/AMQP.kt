package dk.sdu.imagehost.imagestorage.ampq

import com.beust.klaxon.Klaxon
import com.rabbitmq.client.*
import com.rabbitmq.client.AMQP
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
        listeningChannel.queueBind("ImageStorage", "rapid", "ConfirmOnePostCreation")
        listeningChannel.queueBind("ImageStorage", "rapid", "ConfirmOnePostDeletion")
        listeningChannel.queueBind("ImageStorage", "rapid", "ImageLoadRequest")

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
            "ConfirmOnePostCreation" -> klaxon.parse<ImageStorageEvent.Request.Create>(body)!!
            "ConfirmOnePostDeletion" -> klaxon.parse<ImageStorageEvent.Request.Delete>(body)!!
            "ConfirmOnePostUpdate" -> klaxon.parse<ImageStorageEvent.Request.Create>(body)!! //TODO
            else -> return
        }
        val correlation = delivery.properties.correlationId
        callback(event, send(correlation))
    }

    fun send(correlationId: String) = fun (response: ImageStorageEvent.Response) {
        val json: String = klaxon.toJsonString(response)
        println("Sending message:\n\t${response.TAG}\n\t$json")
        val props = AMQP.BasicProperties.Builder().run {
            this.correlationId(correlationId)
            this.headers(mapOf("status_code" to response.status_code.toString()))
            build()
        }
        responseChannel.basicPublish("rapid", response.TAG, props, json.toByteArray());
    }

    companion object {
        private val CANCEL_NOOP = CancelCallback { }
    }

}
