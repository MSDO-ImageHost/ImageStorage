package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.ampq.AMQP
import dk.sdu.imagehost.imagestorage.ampq.EventCallback
import dk.sdu.imagehost.imagestorage.ampq.ImageStorageEvent
import dk.sdu.imagehost.imagestorage.db.Parameters
import java.net.URI
import java.time.LocalDateTime
import java.util.*

fun main() {
    val service = ImageStorageService(Parameters.env(), "img")
    val uri: String = System.getenv("AMQP_URI") ?: "amqp://guest:guest@localhost:5672"

    val callback = EventHandler(service)
    val ampq = AMQP(URI(uri), callback)
}