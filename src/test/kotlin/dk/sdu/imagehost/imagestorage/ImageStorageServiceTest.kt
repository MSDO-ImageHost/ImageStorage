package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.db.Credentials
import dk.sdu.imagehost.imagestorage.db.Parameters
import org.joda.time.DateTime
import org.junit.jupiter.api.*
import java.io.File
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object ImageStorageServiceTest {

    val folderName = "testStore"
    lateinit var imageStorage: ImageStorageService

    lateinit var lighthouseData: ByteArray
    lateinit var pancakesData: ByteArray
    lateinit var rocketData: ByteArray

    val lighthouseID = UUID.randomUUID()
    val pancakesID = UUID.randomUUID()
    val rocketID = UUID.randomUUID()
    val imgID = UUID.randomUUID()
    val rumplestiltkinID = UUID.randomUUID()

    val userA = UUID.randomUUID()
    val userB = UUID.randomUUID()

    @BeforeAll
    fun clean() {
        File(".", folderName).deleteRecursively()
        File("test.sqlite").delete()
    }

    @BeforeAll
    fun loadResources() {
        val classloader = ImageStorageServiceTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouseData = loadResource("lighthouse.png")
        pancakesData = loadResource("pancakes.png")
        rocketData = loadResource("rocket.png")
    }

    @BeforeEach
    fun setUp() {
        File("test.sqlite").delete()
        val credentials = Credentials("root", "")
        val parameters = Parameters(credentials, "jdbc:sqlite:test.sqlite", "org.sqlite.JDBC")
        imageStorage = ImageStorageService(parameters, folderName)
    }

    @AfterEach
    fun tearDown() {
        File(".", folderName).listFiles()!!.forEach {
            it.delete()
        }
        File("test.sqlite").delete()
    }

    @Test
    fun `the store is empty at the start`() {
        assert(imageStorage.allIDs().isEmpty())
        assert(imageStorage.all().isEmpty())
    }

    @Test
    fun `an image is saved`() {
        val lighthouse = Image(lighthouseID, userA, DateTime.now(), lighthouseData)
        imageStorage.storeImage(lighthouse)
        Assertions.assertTrue(imageStorage.exists(lighthouseID))
    }

    @Test
    fun `an image is loaded`() {
        val pancakes = Image(pancakesID, userA, DateTime.now(), pancakesData)
        imageStorage.storeImage(pancakes)
        Assertions.assertTrue(imageStorage.exists(pancakesID))
        val pancakesLoaded = imageStorage.requestImage(pancakesID)
        Assertions.assertEquals(pancakes, pancakesLoaded)
    }

    @Test
    fun `an image is overwritten`() {
        val lighthouse = Image(imgID, userA, DateTime.now(), lighthouseData)
        val pancakes = Image(imgID, userA, DateTime.now(), pancakesData)
        imageStorage.storeImage(pancakes)
        Assertions.assertTrue(imageStorage.exists(imgID))
        imageStorage.storeImage(lighthouse)
        Assertions.assertTrue(imageStorage.exists(imgID))
        val img = imageStorage.requestImage(imgID)
        Assertions.assertEquals(lighthouse, img)
    }

    @Test
    fun `reading a nonexistent image returns null`() {
        Assertions.assertFalse(imageStorage.exists(rumplestiltkinID))
        Assertions.assertNull(imageStorage.requestImage(rumplestiltkinID))
    }

    @Test
    fun `an image is deleted`() {
        val lighthouse = Image(lighthouseID, userA, DateTime.now(), lighthouseData)
        imageStorage.storeImage(lighthouse)
        Assertions.assertTrue(imageStorage.exists(lighthouseID))
        imageStorage.deleteImage(lighthouseID)
        Assertions.assertFalse(imageStorage.exists(lighthouseID))
    }

    @Test
    fun `there are three images saved`() {
        val lighthouse = Image(lighthouseID, userA, DateTime.now(), lighthouseData)
        val pancakes = Image(pancakesID, userB, DateTime.now(), pancakesData)
        val rocket = Image(rocketID, userB, DateTime.now(), rocketData)
        imageStorage.storeImage(lighthouse)
        imageStorage.storeImage(pancakes)
        imageStorage.storeImage(rocket)
        val images = imageStorage.all().toSet()
        Assertions.assertEquals(3, images.size)
        Assertions.assertEquals(setOf(pancakes, lighthouse, rocket), images)
    }
}