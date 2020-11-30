package dk.sdu.imagehost.imagestorage

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.ConnectionFactory
import org.json.JSONObject


class Emit(exchange: String) {
        private val exchangeName = exchange

    fun emitter(argv: Pair<String, JSONObject>) {
        val factory = ConnectionFactory()
        val uri = System.getenv("AMQP_URI")
        if(uri == null) {
            factory.host = "localhost"
        } else {
            factory.setUri(uri)
        }
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

