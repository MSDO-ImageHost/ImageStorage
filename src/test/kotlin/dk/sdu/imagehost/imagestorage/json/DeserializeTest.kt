package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Klaxon
import dk.sdu.imagehost.imagestorage.Image
import dk.sdu.imagehost.imagestorage.ampq.ImageStorageEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object DeserializeTest {

    lateinit var klaxon: Klaxon

    @BeforeAll
    fun setUp() {
        klaxon = Klaxon().converter(Base64Converter).converter(DateTimeConverter).converter(UUIDConverter)
    }

    @Test
    fun `Deserialize Image`() {
        val json = readJson("Image")
        testDeserializeSerialize<Image>(json)
    }

    @Test
    fun `Deserialize ImageCreateRequest`(){
        val json = readJson("ImageCreateRequest")
        testDeserializeSerialize<ImageStorageEvent.Request.Create>(json)
    }

    @Test
    fun `Deserialize ImageCreateRequest with extra ignored fields`(){
        val json = readJson("ConfirmOnePostCreation")
        val instance = klaxon.parse<ImageStorageEvent.Request.Create>(json)
        SerializeTest.testSerializeDeserialize(instance)
    }

    @Test
    fun `Deserialize ImageLoadRequest`(){
        val json = readJson("ImageLoadRequest")
        testDeserializeSerialize<ImageStorageEvent.Request.Load>(json)
    }

    @Test
    fun `Deserialize ImageDeleteRequest`(){
        val json = readJson("ImageDeleteRequest")
        testDeserializeSerialize<ImageStorageEvent.Request.Delete>(json)
    }

    @Test
    fun `Deserialize ImageCreateResponse`(){
        val json = readJson("ImageCreateResponse")
        testDeserializeSerialize<ImageStorageEvent.Response.Create>(json)
    }

    @Test
    fun `Deserialize ImageLoadResponse`(){
        val json = readJson("ImageLoadResponse")
        testDeserializeSerialize<ImageStorageEvent.Response.Load>(json)
    }

    @Test
    fun `Deserialize ImageDeleteResponse`(){
        val json = readJson("ImageDeleteResponse")
        testDeserializeSerialize<ImageStorageEvent.Response.Delete>(json)
    }

    @Test
    fun `Deserialize ImageLoadError`(){
        val json = readJson("ImageLoadError")
        testDeserializeSerialize<ImageStorageEvent.Response.LoadError>(json)
    }

    fun readJson(name: String) = DeserializeTest::class.java.classLoader.getResource("$name.json")!!.readText().trim()

    inline fun <reified T> testDeserializeSerialize(json: String){
        val instance = klaxon.parse<T>(json)
        val encoded = klaxon.toJsonString(instance)
        assertEquals(json, encoded)
    }

}