package dk.sdu.imagehost.imagestorage.ampq

import com.rabbitmq.client.*
import java.io.Closeable
import java.net.URI

class AMPQ(val uri: URI) : Closeable {
    val connection: Connection = ConnectionFactory().run {
        setUri(uri)
        newConnection()
    }
    val channel = connection.createChannel().apply {
        exchangeDeclare("rapid", "direct")
    }

    override fun close() {
        channel.close()
        connection.close()
    }

}