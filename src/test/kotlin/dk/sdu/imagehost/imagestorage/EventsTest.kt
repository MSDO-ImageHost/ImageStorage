package dk.sdu.imagehost.imagestorage

import org.json.JSONObject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class EventsTest {

    @Test
    fun `request store image`() {
        val imageData = "hi".toByteArray()
        val message = Events().requestStoreImage("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a", imageData)
        val obj = JSONObject("{\"creator\":\"42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a\",\"imageData\":[104,105]}")
        val answer = Pair("storeImage", obj)

        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }

    @Test
    fun `request store image with imageID`() {
        val imageData = "hi".toByteArray()
        val message = Events().requestStoreImage("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a", imageData, "e9e58c72-d14d-4378-a146-d91dfe03523c")
        val obj = JSONObject("{\"creator\":\"42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a\",\"imageID\":\"e9e58c72-d14d-4378-a146-d91dfe03523c\",\"imageData\":[104,105]}")
        val answer = Pair("storeImage", obj)

        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }

    @Test
    fun responseStoreImage() {
        val message = Events().responseStoreImage(200, "rudolf", "potato")
        val obj = JSONObject("{\"status_code\":200,\"imageID\":\"potato\",\"created_by\":\"rudolf\"}")
        val answer = Pair("storeImage", obj)
        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }

    @Test
    fun requestRequestImage() {
        val message = Events().requestRequestImage("e9e58c72-d14d-4378-a146-d91dfe03523c")
        val obj = JSONObject("{\"imageID\":\"e9e58c72-d14d-4378-a146-d91dfe03523c\"}")
        val answer = Pair("requestImage", obj)
        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }

    @Test
    fun responseRequestImage() {
        val message = Events().responseRequestImage(400, null)
        val obj = JSONObject("{\"status_code\":400}")
        val answer = Pair("requestImage", obj)
        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }

    @Test
    fun requestDeleteImage() {
        val message = Events().requestDeleteImage("e9e58c72-d14d-4378-a146-d91dfe03523c", "rudolf")
        val obj = JSONObject("{\"imageID\":\"e9e58c72-d14d-4378-a146-d91dfe03523c\",\"deletor\":\"rudolf\"}")
        val answer = Pair("deleteImage", obj)
        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }

    @Test
    fun responseDeleteImage() {
        val message = Events().responseDeleteImage(200)
        val obj = JSONObject("{\"status_code\":200}")
        val answer = Pair("deleteImage", obj)
        assertEquals(answer.first, message.first)
        assertEquals(answer.second.toString(), message.second.toString())
    }
}