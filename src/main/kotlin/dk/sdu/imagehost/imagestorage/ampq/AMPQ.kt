package dk.sdu.imagehost.imagestorage.ampq

import com.rabbitmq.client.*
import java.io.Closeable
import java.net.URI

class AMPQ(val uri: URI, val callback: EventCallback) : Closeable {

    val connection: Connection = ConnectionFactory().run {
        setUri(uri)
        newConnection()
    }

    val responseChannel = connection.createChannel().apply {
        exchangeDeclare("rapid", "direct")
        queueDeclare("ImageStorage", false, false, false, emptyMap())
    }

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
        //TODO: Parse JSON from request body
        //TODO: Call callback
    }

    fun requestLoad(consumerTag: String, delivery: Delivery) {
        //TODO: Parse JSON from request body
        //TODO: Call callback
    }

    fun requestDelete(consumerTag: String, delivery: Delivery) {
        //TODO: Parse JSON from request body
        //TODO: Call callback
    }

    @Suppress("UNREACHABLE_CODE")
    fun send(response: ImageStorageEvent.Response) {
        val json: String = TODO("Convert response to JSON")
        responseChannel.basicPublish("rapid", response.TAG, null, json.toByteArray());
    }

    companion object {
        private val CANCEL_NOOP = CancelCallback { }
    }

}
