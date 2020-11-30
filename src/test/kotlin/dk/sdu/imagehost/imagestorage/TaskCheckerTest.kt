package dk.sdu.imagehost.imagestorage

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TaskCheckerTest {
    private lateinit var taskChecker: TaskChecker
    private lateinit var lighthouseData: ByteArray
    private lateinit var pancakesData: ByteArray
    private lateinit var rocketData: ByteArray
    private val standardOut = System.out
    private lateinit var outputStreamCaptor: ByteArrayOutputStream

    @BeforeAll
    fun loadResources() {
        val classloader = TaskCheckerTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouseData = loadResource("lighthouse.png")
        pancakesData = loadResource("pancakes.png")
        rocketData = loadResource("rocket.png")
    }

    @BeforeAll
    fun clean() {
        File(".", ImageStorageServiceTest.folderName).deleteRecursively()
        File("test.sqlite").delete()
    }

    @BeforeEach
    fun setUp() {
        taskChecker = TaskChecker()
        outputStreamCaptor = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStreamCaptor))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(standardOut)
        File(".", ImageStorageServiceTest.folderName).listFiles()!!.forEach {
            it.delete()
        }
        File("test.sqlite").delete()
    }

    @Test
    fun `storing image 200`() {
        val imageID = UUID.fromString("dfbfeb97-c35d-447c-b55c-a7e423d6e2e1")
        val userID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val answer = "storing image\r\n" +
                " [x] Sent 'storeImage':'{\"status_code\":200,\"imageID\":\"dfbfeb97-c35d-447c-b55c-a7e423d6e2e1\",\"created_by\":\"42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a\"}'"
        val message = Events().requestStoreImage(userID.toString(), lighthouseData, imageID.toString())
        taskChecker.taskChecker(message)
        val actual = outputStreamCaptor.toString().trim()
        assertEquals(answer, actual)
    }

    @Test
    fun `storing image 400`() {
        val imageID = UUID.fromString("dfbfeb97-c35d-447c-b55c-a7e423d6e2e1")
        val userID = null
        val answer = "storing image\r\n" +
                " [x] Sent 'storeImage':'{\"status_code\":400}'"
        val message = Events().requestStoreImage(userID.toString(), lighthouseData, imageID.toString())
        taskChecker.taskChecker(message)
        val actual = outputStreamCaptor.toString().trim()
        assertEquals(answer, actual)
    }

    @Test
    fun `requesting image 200`() {
        val imageID = UUID.fromString("dfbfeb97-c35d-447c-b55c-a7e423d6e2e1")
        val userID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val message1 = Events().requestStoreImage(userID.toString(), lighthouseData, imageID.toString())
        taskChecker.taskChecker(message1)

        val message2 = Events().requestRequestImage(imageID.toString())
        val answer = "storing image\r\n" +
                " [x] Sent 'storeImage':'{\"status_code\":200,\"imageID\":\"dfbfeb97-c35d-447c-b55c-a7e423d6e2e1\",\"created_by\":\"42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a\"}'\r\n" +
                "requesting image\r\n" +
                " [x] Sent 'requestImage':'{\"status_code\":200,\"Image\":\"Image(id=dfbfeb97-c35d-447c-b55c-a7e423d6e2e1, owner=42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a,"
        taskChecker.taskChecker(message2)
        val actual = outputStreamCaptor.toString().trim().substring(0, 324)
        assertEquals(answer, actual)
    }

    @Test
    fun `requesting image 404`() {
        val imageID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val message = Events().requestRequestImage(imageID.toString())
        val answer = "requesting image\r\n" +
                " [x] Sent 'requestImage':'{\"status_code\":404}'"
        taskChecker.taskChecker(message)
        val actual = outputStreamCaptor.toString().trim()
        assertEquals(answer, actual)
    }

    @Test
    fun `requesting image 400`() {
        val imageID = "not a UUID"
        val message = Events().requestRequestImage(imageID)
        val answer = "requesting image\r\n" +
                " [x] Sent 'requestImage':'{\"status_code\":400}'"
        taskChecker.taskChecker(message)
        val actual = outputStreamCaptor.toString().trim()
        assertEquals(answer, actual)
    }

    @Test
    fun `deleting image 200`() {
        val imageID = UUID.fromString("cabfeb97-c35d-447c-b55c-a7e423d6e2e1")
        val userID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val message1 = Events().requestStoreImage(userID.toString(), lighthouseData, imageID.toString())
        taskChecker.taskChecker(message1)

        val message2 = Events().requestDeleteImage(imageID.toString(), userID.toString())
        taskChecker.taskChecker(message2)

        val lines = outputStreamCaptor.toString().trim().lines()

        val answer = "deleting image" + " [x] Sent 'deleteImage':'{\"status_code\":200}'"
        val actual = lines[2] + lines[3]
        assertEquals(answer, actual)
    }

    @Test
    fun `deleting image 404`() {
        val imageID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val userID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val message = Events().requestDeleteImage(imageID.toString(), userID.toString())
        val answer = "deleting image\r\n" +
                " [x] Sent 'deleteImage':'{\"status_code\":404}'"
        taskChecker.taskChecker(message)
        val actual = outputStreamCaptor.toString().trim()
        assertEquals(answer, actual)
    }

    @Test
    fun `deleting image 400`() {
        val imageID = "not a uuid"
        val userID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val message = Events().requestDeleteImage(imageID, userID.toString())
        val answer = "deleting image\r\n" +
                " [x] Sent 'deleteImage':'{\"status_code\":400}'"
        taskChecker.taskChecker(message)
        val actual = outputStreamCaptor.toString().trim()
        assertEquals(answer, actual)
    }

    @Test
    fun `storing requesting deleting image 200`() {
        val imageID = UUID.fromString("c0d8d1e2-676c-41c0-8093-8b5b6cae607c")
        val userID = UUID.fromString("42b7fa9a-5746-4bc1-97cd-1fbee6a2fe8a")
        val message1 = Events().requestStoreImage(userID.toString(), lighthouseData, imageID.toString())
        taskChecker.taskChecker(message1)

        val message2 = Events().requestRequestImage(imageID.toString())
        taskChecker.taskChecker(message2)

        val message3 = Events().requestDeleteImage(imageID.toString(), userID.toString())
        taskChecker.taskChecker(message3)

        val lines = outputStreamCaptor.toString().trim().lines()

        val answer = "deleting image" + " [x] Sent 'deleteImage':'{\"status_code\":200}'"
        val actual = lines[4] + lines[5]
        assertEquals(answer, actual)
    }
}