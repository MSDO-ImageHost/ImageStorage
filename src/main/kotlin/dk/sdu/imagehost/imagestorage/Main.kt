package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.ampq.AMPQ
import dk.sdu.imagehost.imagestorage.ampq.EventCallback
import dk.sdu.imagehost.imagestorage.ampq.ImageStorageEvent
import dk.sdu.imagehost.imagestorage.db.Parameters
import java.net.URI
import java.time.LocalDateTime
import java.util.*

fun main() {
    val service = ImageStorageService(Parameters.env(), "img")

    val callback = object : EventCallback {
        override fun invoke(req: ImageStorageEvent.Request, res: (ImageStorageEvent.Response) -> Unit) {
            when (req) {
                is ImageStorageEvent.Request.Create -> {
                    val id = UUID.randomUUID()
                    val image = Image(id, req.owner, LocalDateTime.now(), req.data)
                    service.storeImage(image)
                    res(ImageStorageEvent.Response.Create(id))
                }
                is ImageStorageEvent.Request.Load -> {
                    val image = service.requestImage(req.id)
                    if (image == null) {
                        res(ImageStorageEvent.Response.LoadError(req.id))
                    } else {
                        val (id, owner, createdAt, data) = image
                        res(ImageStorageEvent.Response.Load(id, owner, data, createdAt))
                    }
                }
                is ImageStorageEvent.Request.Delete -> {
                    service.deleteImage(req.id)
                    res(ImageStorageEvent.Response.Delete(req.id))
                }
            }
        }
    }
    val ampq = AMPQ(URI(System.getenv("AMPQ_URI")), callback)
}