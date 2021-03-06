package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.db.Credentials
import dk.sdu.imagehost.imagestorage.db.Parameters
import org.junit.jupiter.api.*
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object ImageStorageServiceTest {

    val folderName = "testStore"
    lateinit var imageStorage: ImageStorageService

    lateinit var lighthouseData: ByteArray
    lateinit var pancakesData: ByteArray
    lateinit var rocketData: ByteArray

    val lighthouseID = UUID.randomUUID().toString()
    val pancakesID = UUID.randomUUID().toString()
    val rocketID = UUID.randomUUID().toString()
    val imgID = UUID.randomUUID().toString()
    val rumplestiltkinID = UUID.randomUUID().toString()

    val userA = UUID.randomUUID().toString()
    val userB = UUID.randomUUID().toString()

    val NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)

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
        val lighthouse = Image(lighthouseID, NOW, lighthouseData)
        imageStorage.storeImage(lighthouse)
        Assertions.assertTrue(imageStorage.exists(lighthouseID))
    }

    @Test
    fun `an image is loaded`() {
        val pancakes = Image(pancakesID, NOW, pancakesData)
        imageStorage.storeImage(pancakes)
        Assertions.assertTrue(imageStorage.exists(pancakesID))
        val pancakesLoaded = imageStorage.requestImage(pancakesID)
        Assertions.assertEquals(pancakes, pancakesLoaded)
    }

    @Test
    fun `an image is overwritten`() {
        val lighthouse = Image(imgID, NOW.truncatedTo(ChronoUnit.MILLIS), lighthouseData)
        val pancakes = Image(imgID, NOW, pancakesData)
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
        val lighthouse = Image(lighthouseID, NOW, lighthouseData)
        imageStorage.storeImage(lighthouse)
        Assertions.assertTrue(imageStorage.exists(lighthouseID))
        imageStorage.deleteImage(lighthouseID)
        Assertions.assertFalse(imageStorage.exists(lighthouseID))
    }

    @Test
    fun `there are three images saved`() {
        val lighthouse = Image(lighthouseID, NOW, lighthouseData)
        val pancakes = Image(pancakesID, NOW, pancakesData)
        val rocket = Image(rocketID, NOW, rocketData)
        imageStorage.storeImage(lighthouse)
        imageStorage.storeImage(pancakes)
        imageStorage.storeImage(rocket)
        val images = imageStorage.all().toSet()
        Assertions.assertEquals(3, images.size)
        Assertions.assertEquals(setOf(pancakes, lighthouse, rocket), images)
    }
}