package dk.sdu.imagehost.imagestorage

import org.json.JSONObject

class Events {

    fun requestStoreImage(creator: String, imageData: ByteArray, imageID: String? = null): Pair<String,JSONObject> {
        val message = JSONObject()
        message.put("creator", creator)
        message.put("imageData", imageData)
        if (imageID != null) {
            message.put("imageID", imageID)
        }
        return Pair("storeImage", message)
    }

    fun responseStoreImage(status_code: Int, created_by: String?, imageID: String? = null): Pair<String,JSONObject> {
        val message = JSONObject()
        message.put("status_code", status_code)
        message.put("created_by", created_by)
        if (imageID != null) {
            message.put("imageID", imageID)
        }
        return Pair("storeImage", message)
    }

    fun requestRequestImage(imageID: String): Pair<String,JSONObject> {
        val message = JSONObject()
        message.put("imageID", imageID)
        return Pair("requestImage", message)
    }

    fun responseRequestImage(status_code: Int, Image: Image?): Pair<String,JSONObject> {
        val message = JSONObject()
        message.put("status_code", status_code)
        message.put("Image", Image)
        return Pair("requestImage", message)
    }

    fun requestDeleteImage(imageID: String, deletor: String): Pair<String,JSONObject> {
        val message = JSONObject()
        message.put("imageID", imageID)
        message.put("deletor", deletor)
        return Pair("deleteImage", message)
    }

    fun responseDeleteImage(status_code: Int): Pair<String,JSONObject> {
        val message = JSONObject()
        message.put("status_code", status_code)
        return Pair("deleteImage", message)
    }


}