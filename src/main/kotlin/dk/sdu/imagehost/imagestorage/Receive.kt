package dk.sdu.imagehost.imagestorage

import com.rabbitmq.client.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class Receive(exchange: String) {
    private val exchangeName = exchange
    var uri: String = System.getenv("AMQP_URI") ?: "localhost"

    fun receiver(keywords: MutableList<String>) {
        val factory = ConnectionFactory()
        factory.setUri(uri)
        val connection = factory.newConnection()
        val channel = connection.createChannel()


        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT)
        val queueName = channel.queueDeclare().queue

        for (keyword in keywords) {
            channel.queueBind(queueName, exchangeName, keyword)
        }

        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(consumerTag: String, envelope: Envelope,
                                        properties: AMQP.BasicProperties, body: ByteArray) {
                val event = String(body, charset("UTF-8"))
                println(" [x] Received '" + envelope.routingKey + "':'" + event + "'")
                GlobalScope.launch {
                    val ev = JSONObject(event)
                    TaskChecker().taskChecker(Pair(envelope.routingKey, ev))
                }
            }
        }
        channel.basicConsume(queueName, true, consumer)
    }
}

