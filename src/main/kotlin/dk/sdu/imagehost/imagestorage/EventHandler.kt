package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.ampq.EventCallback
import dk.sdu.imagehost.imagestorage.ampq.ImageStorageEvent
import java.time.LocalDateTime

class EventHandler(val service: ImageStorageService) : EventCallback {
    override fun invoke(req: ImageStorageEvent.Request, res: (ImageStorageEvent.Response) -> Unit) {
        when (req) {
            is ImageStorageEvent.Request.Create -> {
                val image = Image(req.post_id, LocalDateTime.now(), req.image_data)
                service.storeImage(image)
                res(ImageStorageEvent.Response.Create(req.post_id))
            }
            is ImageStorageEvent.Request.Load -> {
                val image = service.requestImage(req.post_id)
                if (image == null) {
                    res(ImageStorageEvent.Response.LoadError(req.post_id))
                } else {
                    val (id, createdAt, data) = image
                    res(ImageStorageEvent.Response.Load(id, data, createdAt))
                }
            }
            is ImageStorageEvent.Request.Delete -> {
                service.deleteImage(req.post_id)
                res(ImageStorageEvent.Response.Delete(req.post_id))
            }
        }
    }
}