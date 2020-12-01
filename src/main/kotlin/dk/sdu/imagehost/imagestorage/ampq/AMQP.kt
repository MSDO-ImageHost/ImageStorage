package dk.sdu.imagehost.imagestorage.ampq

import com.beust.klaxon.Klaxon
import com.rabbitmq.client.*
import dk.sdu.imagehost.imagestorage.json.Base64Converter
import dk.sdu.imagehost.imagestorage.json.DateTimeConverter
import dk.sdu.imagehost.imagestorage.json.UUIDConverter
import java.io.Closeable
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
        subscribe("ImageCreateRequest", DeliverCallback(::requestCreate))
        subscribe("ImageLoadRequest", DeliverCallback(::requestLoad))
        subscribe("ImageDeleteRequest", DeliverCallback(::requestDelete))
    }

    private fun subscribe(event: String, deliverCallback: DeliverCallback) {
        val listeningChannel = connection.createChannel().apply {
            exchangeDeclare("rapid", "direct")
            queueDeclare("ImageStorage", false, false, false, emptyMap())
        }
        listeningChannel.queueBind("ImageStorage", "rapid", event)
        listeningChannel.basicConsume("ImageStorage", true, deliverCallback, CANCEL_NOOP)
    }

    override fun close() {
        connection.close()
    }

    fun requestCreate(consumerTag: String, delivery: Delivery) {
        val event = klaxon.parse<ImageStorageEvent.Request.Create>(String(delivery.body))!!
        callback(event, ::send)
    }

    fun requestLoad(consumerTag: String, delivery: Delivery) {
        val event = klaxon.parse<ImageStorageEvent.Request.Load>(String(delivery.body))!!
        callback(event, ::send)
    }

    fun requestDelete(consumerTag: String, delivery: Delivery) {
        val event = klaxon.parse<ImageStorageEvent.Request.Delete>(String(delivery.body))!!
        callback(event, ::send)
    }

    @Suppress("UNREACHABLE_CODE")
    fun send(response: ImageStorageEvent.Response) {
        val json: String = klaxon.toJsonString(response)
        responseChannel.basicPublish("rapid", response.TAG, null, json.toByteArray());
    }

    companion object {
        private val CANCEL_NOOP = CancelCallback { }
    }

}
