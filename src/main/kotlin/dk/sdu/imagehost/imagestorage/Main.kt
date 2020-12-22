package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.ampq.AMQP
import dk.sdu.imagehost.imagestorage.db.Parameters

fun main() {
    println("Starting ImageStorageService...")
    val service = ImageStorageService(Parameters.env(), System.getenv("IMG_DIR") ?: "img")

    val callback = EventHandler(service)
    println("Connecting to AMQP")
    val ampq = AMQP(AMQP.Parameters.env(), callback)
}