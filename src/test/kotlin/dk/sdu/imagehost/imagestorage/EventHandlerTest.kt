package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.ampq.ImageStorageEvent
import dk.sdu.imagehost.imagestorage.db.Credentials
import dk.sdu.imagehost.imagestorage.db.Parameters
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object EventHandlerTest {

    private lateinit var lighthouseData: ByteArray
    val folderName = "testStore"

    @BeforeAll
    fun setup(){
        val classloader = EventHandlerTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouseData = loadResource("lighthouse.png")
    }

    private lateinit var handler: EventHandler

    @BeforeAll
    fun clean() {
        File(".", folderName).deleteRecursively()
        File("test.sqlite").delete()
    }

    @BeforeAll
    fun loadResources() {
        val classloader = EventHandlerTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouseData = loadResource("lighthouse.png")
    }

    @BeforeEach
    fun setUp() {
        File("test.sqlite").delete()
        val credentials = Credentials("root", "")
        val parameters = Parameters(credentials, "jdbc:sqlite:test.sqlite", "org.sqlite.JDBC")
        val service = ImageStorageService(parameters, ImageStorageServiceTest.folderName)
        handler = EventHandler(service)
    }

    @AfterEach
    fun tearDown() {
        File(".", ImageStorageServiceTest.folderName).listFiles()!!.forEach {
            it.delete()
        }
        File("test.sqlite").delete()
    }

    class EventHolder : (ImageStorageEvent.Response) -> Unit {

        var lastEvent: ImageStorageEvent? = null
            get() = field
            private set(value) {
                field = value
            }

        override fun invoke(input: ImageStorageEvent.Response) {
            lastEvent = input
        }

    }

    @Test
    fun `ImageCreateRequest reply with ImageCreateResponse`(){
        val owner = UUID.randomUUID()
        val id = UUID.randomUUID()
        val request = ImageStorageEvent.Request.Create(id, owner, lighthouseData)
        val holder = EventHolder()
        handler(request, holder)
        val response = holder.lastEvent
        assert(response is ImageStorageEvent.Response.Create)
    }

    @Test
    fun `ImageDeleteRequest reply with ImageDeleteResponse`(){
        val id = UUID.randomUUID()
        val request = ImageStorageEvent.Request.Delete(id)
        val holder = EventHolder()
        handler(request, holder)
        val response = holder.lastEvent
        assert(response is ImageStorageEvent.Response.Delete)
    }

    @Test
    fun `ImageLoadRequest for existing image reply with ImageLoadResponse`(){
        val owner = UUID.randomUUID()
        val create = ImageStorageEvent.Request.Create(UUID.randomUUID(), owner, lighthouseData)
        val holder = EventHolder()
        handler(create, holder)
        val createResponse = holder.lastEvent
        assert(createResponse is ImageStorageEvent.Response.Create)
        createResponse as ImageStorageEvent.Response.Create
        val id = createResponse.id

        val load = ImageStorageEvent.Request.Load(id)
        handler(load, holder)
        val loadResponse = holder.lastEvent
        assert(loadResponse is ImageStorageEvent.Response.Load)
        loadResponse as ImageStorageEvent.Response.Load
        assertArrayEquals(lighthouseData, loadResponse.data)
        assertEquals(owner, loadResponse.owner)
        assertEquals(id, loadResponse.id)
    }

    @Test
    fun `ImageLoadRequest for a nonexistant image reply with ImageLoadErrorResponse`(){
        val holder = EventHolder()
        val id = UUID.randomUUID()
        val load = ImageStorageEvent.Request.Load(id)
        handler(load, holder)
        val loadResponse = holder.lastEvent
        assert(loadResponse is ImageStorageEvent.Response.LoadError)
        loadResponse as ImageStorageEvent.Response.LoadError
        assertEquals(id, loadResponse.id)
    }

}