import dk.sdu.imagehost.imagestorage.FileSystemImageStore
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.FileNotFoundException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object FileSystemImageStoreTest {

    val folderName = "testStore"
    val folder = File(".", folderName)
    lateinit var imageStore: FileSystemImageStore
    lateinit var lighthouse: ByteArray
    lateinit var pancakes: ByteArray
    lateinit var rocket: ByteArray

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
        imageStore.save(lighthouse, "lighthouse")
        val file = File(folder, "lighthouse.png")
        assertTrue(imageStore.exists("lighthouse"))
        assertTrue(file.exists())
        assertTrue(file.totalSpace >= lighthouse.size)
    }

    @Test
    fun `an image is loaded`(){
        imageStore.save(pancakes, "pancakes")
        val file = File(folder, "pancakes.png")
        assertTrue(imageStore.exists("pancakes"))
        assertTrue(file.exists())
        assertTrue(file.totalSpace >= lighthouse.size)
        val readout = imageStore.load("pancakes")
        assertEquals(pancakes.size, readout.size)
        assertArrayEquals(pancakes, readout)
    }

    @Test
    fun `an image is overwritten`(){
        imageStore.save(pancakes, "img")
        assertTrue(imageStore.exists("img"))
        imageStore.save(lighthouse, "img")
        assertTrue(imageStore.exists("img"))
        val img = imageStore.load("img")
        assertArrayEquals(lighthouse, img)
    }

    @Test
    fun `reading a nonexistent image produces an error`(){
        assertFalse(imageStore.exists("rumplestiltkin"))
        assertThrows<FileNotFoundException> { imageStore.load("rumplestiltkin") }
    }

    @Test
    fun `an image is deleted`(){
        imageStore.save(pancakes, "pancakes")
        assertTrue(imageStore.exists("pancakes"))
        imageStore.delete("pancakes")
        assertFalse(imageStore.exists("pancakes"))
    }

    @Test
    fun `there are three images saved`(){
        imageStore.save(pancakes, "pancakes")
        imageStore.save(lighthouse, "lighthouse")
        imageStore.save(rocket, "rocket")
        val images = imageStore.listFiles().toSet()
        assertEquals(3, images.size)
        assertEquals(setOf("pancakes", "lighthouse", "rocket"), images)
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