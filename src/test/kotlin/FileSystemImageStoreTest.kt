import dk.sdu.imagehost.imagestorage.FileSystemImageStore
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.FileNotFoundException
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object FileSystemImageStoreTest {

    val folderName = "testStore"
    val folder = File(".", folderName)
    val fileEnding = ".png"
    lateinit var imageStore: FileSystemImageStore
    lateinit var lighthouse: ByteArray
    lateinit var pancakes: ByteArray
    lateinit var rocket: ByteArray

    val lighthouseID = UUID.randomUUID()
    val pancakesID = UUID.randomUUID()
    val rocketID = UUID.randomUUID()
    val imgID = UUID.randomUUID()
    val rumplestiltkinID = UUID.randomUUID()

    @BeforeAll
    fun setup(){
        File(".", folderName).deleteRecursively()

        val classloader = FileSystemImageStoreTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouse = loadResource("lighthouse.png")
        pancakes = loadResource("pancakes.png")
        rocket = loadResource("rocket.png")

        imageStore = FileSystemImageStore(folderName)
    }

    @Test
    fun `the store is empty at the start`(){
        assert(imageStore.listFiles().isEmpty())
    }

    @Test
    fun `an image is saved`(){
        imageStore.save(lighthouse, lighthouseID)
        val file = File(folder, lighthouseID.toString() + fileEnding)
        assertTrue(imageStore.exists(lighthouseID))
        assertTrue(file.exists())
        assertTrue(file.totalSpace >= lighthouse.size)
    }

    @Test
    fun `an image is loaded`(){
        imageStore.save(pancakes, pancakesID)
        val file = File(folder, pancakesID.toString() + fileEnding)
        assertTrue(imageStore.exists(pancakesID))
        assertTrue(file.exists())
        assertTrue(file.totalSpace >= pancakes.size)
        val readout = imageStore.load(pancakesID)
        assertEquals(pancakes.size, readout.size)
        assertArrayEquals(pancakes, readout)
    }

    @Test
    fun `an image is overwritten`(){
        imageStore.save(pancakes, imgID)
        assertTrue(imageStore.exists(imgID))
        imageStore.save(lighthouse, imgID)
        assertTrue(imageStore.exists(imgID))
        val img = imageStore.load(imgID)
        assertArrayEquals(lighthouse, img)
    }

    @Test
    fun `reading a nonexistent image produces an error`(){
        assertFalse(imageStore.exists(rumplestiltkinID))
        assertThrows<FileNotFoundException> { imageStore.load(rumplestiltkinID) }
    }

    @Test
    fun `an image is deleted`(){
        imageStore.save(pancakes, pancakesID)
        assertTrue(imageStore.exists(pancakesID))
        imageStore.delete(pancakesID)
        assertFalse(imageStore.exists(pancakesID))
    }

    @Test
    fun `there are three images saved`(){
        imageStore.save(pancakes, pancakesID)
        imageStore.save(lighthouse, lighthouseID)
        imageStore.save(rocket, rocketID)
        val images = imageStore.listFiles().toSet()
        assertEquals(3, images.size)
        assertEquals(setOf(pancakesID, lighthouseID, rocketID), images)
    }

    @AfterEach
    fun cleanupFiles(){
        File(".", folderName).listFiles()?.forEach { it.delete() }
    }

    @AfterAll
    fun cleanupFolder(){
        File(".", folderName).deleteRecursively()
    }

}