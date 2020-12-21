package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Klaxon
import dk.sdu.imagehost.imagestorage.Image
import dk.sdu.imagehost.imagestorage.ampq.ImageStorageEvent
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object SerializeTest {

    lateinit var klaxon: Klaxon

    private val NOW: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
    private val DATA = ByteArray(64).also { Random().nextBytes(it) }

    @BeforeAll
    fun setUp() {
        klaxon = Klaxon().converter(Base64Converter).converter(DateTimeConverter).converter(UUIDConverter)
    }

    private val id: UUID = UUID.randomUUID()
    private val owner: UUID = UUID.randomUUID()

    @Test
    fun `Serialize Image`() {
        testSerializeDeserialize(Image(id, owner, NOW, DATA))
    }

    @Test
    fun `Serialize ImageCreateRequest`() {
        val event = ImageStorageEvent.Request.Create(id, owner, DATA)
        testSerializeDeserialize(event)
    }

    @Test
    fun `Serialize ImageLoadRequest`() {
        val event = ImageStorageEvent.Request.Load(id)
        testSerializeDeserialize(event)
    }

    @Test
    fun `Serialize ImageDeleteRequest`() {
        val event = ImageStorageEvent.Request.Delete(id)
        testSerializeDeserialize(event)
    }

    @Test
    fun `Serialize ImageCreateResponse`() {
        val event = ImageStorageEvent.Response.Create(id)
        testSerializeDeserialize(event)
    }

    @Test
    fun `Serialize ImageLoadResponse`() {
        val event = ImageStorageEvent.Response.Load(id, owner, DATA, NOW)
        testSerializeDeserialize(event)
    }

    @Test
    fun `Serialize ImageDeleteResponse`() {
        val event = ImageStorageEvent.Response.Delete(id)
        testSerializeDeserialize(event)
    }

    @Test
    fun `Serialize ImageLoadError`() {
        val event = ImageStorageEvent.Response.LoadError(id)
        testSerializeDeserialize(event)
    }

    inline fun <reified T> testSerializeDeserialize(instance: T) {
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<T>(encoded)
        println(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }
}