package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.ampq.AMQP
import dk.sdu.imagehost.imagestorage.db.Parameters

fun main() {
    val service = ImageStorageService(Parameters.env(), "img")

    val callback = EventHandler(service)
    val ampq = AMQP(AMQP.Parameters.env(), callback)
}