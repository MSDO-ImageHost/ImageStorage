package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.db.Credentials
import dk.sdu.imagehost.imagestorage.db.Parameters
import org.joda.time.DateTime
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.util.*

class TaskChecker {
    private val em = Emit("ImageStorage")

    private val credentials = Credentials("root", "")
    private val parameters = Parameters(credentials, "jdbc:sqlite:test.sqlite", "org.sqlite.JDBC")
    private var imageStorage = ImageStorageService(parameters, "testStore")

    fun taskChecker(task: Pair<String, JSONObject>) {
        when (task.first) {
            "storeImage" -> {
                println("storing image")
                var response: Pair<String, JSONObject>
                val message = task.second
                val imageID: UUID
                val user: UUID
                val data: ByteArray

                imageID = if(message.has("imageID")) {
                    UUID.fromString( message.get("imageID").toString())
                } else {
                    UUID.randomUUID()
                }
                if(message.has("creator")) {
                    try {
                        user = UUID.fromString( message.get("creator").toString())
                        if(message.has("imageData")) {
                            data = message.get("imageData").toString().toByteArray()
                            val newImage = Image(imageID, user, DateTime.now(), data)
                            imageStorage.storeImage(newImage)
                            response = Events().responseStoreImage(200, user.toString(), imageID.toString())
                        } else {
                            response = Events().responseStoreImage(400, null, null)
                        }
                    } catch (e: IllegalArgumentException) {
                        response = Events().responseStoreImage(400, null, null)
                    }
                } else {
                    response = Events().responseStoreImage(400, null, null)
                }
                em.emitter(response)
            }
            "requestImage" -> {
                println("requesting image")
                var response: Pair<String, JSONObject>
                val message = task.second
                val imageID: UUID

                 if(message.has("imageID")) {
                     try {
                         imageID = UUID.fromString(message.get("imageID").toString())
                         val out = imageStorage.requestImage(imageID)
                         if (out is Image) {
                             response = Events().responseRequestImage(200, out)
                         } else {
                             response = Events().responseRequestImage(404, out)
                         }
                     } catch (e: IllegalArgumentException) {
                         response = Events().responseRequestImage(400, null)
                     }
                } else {
                     response = Events().responseRequestImage(400, null)
                }
                em.emitter(response)
            }
            "deleteImage" -> {
                println("deleting image")
                var response: Pair<String, JSONObject>
                val message = task.second
                val imageID: UUID

                if(message.has("imageID")) {
                    try {
                        imageID = UUID.fromString( message.get("imageID").toString())
                        val imageExists = imageStorage.exists(imageID)
                        if (imageExists) {
                            imageStorage.deleteImage(imageID)
                            response = Events().responseDeleteImage(200)
                        } else {
                            response = Events().responseDeleteImage(404)
                        }
                    } catch (e: IllegalArgumentException) {
                        response = Events().responseDeleteImage(400)
                    }
                } else {
                    response = Events().responseDeleteImage(400)
                }
                em.emitter(response)
            }
        }
    }

}