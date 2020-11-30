package dk.sdu.imagehost.imagestorage

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.ConnectionFactory
import org.json.JSONObject


class Emit(exchange: String) {
    private val exchangeName = exchange
    var uri: String = System.getenv("AMQP_URI") ?: "amqp://guest:guest@localhost:5672"

    fun emitter(argv: Pair<String, JSONObject>) {
        val factory = ConnectionFactory()
        factory.setUri(uri)
        val connection = factory.newConnection()
        val channel = connection.createChannel()

        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT)

        val keyword = argv.first
        val event = argv.second

        channel.basicPublish(exchangeName, keyword, null, event.toString().toByteArray())
        println(" [x] Sent '$keyword':'$event'")

        channel.close()
        connection.close()
    }
}

