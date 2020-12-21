import dk.sdu.imagehost.imagestorage.db.Credentials
import dk.sdu.imagehost.imagestorage.db.ImageRecord
import dk.sdu.imagehost.imagestorage.db.ImageRecords
import dk.sdu.imagehost.imagestorage.db.Parameters
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object ImageStorageDBTest {

    lateinit var db: Database

    @BeforeAll
    fun setup() {
        File("test.sqlite").delete()
        val credentials = Credentials("root", "")
        val parameters = Parameters(credentials, "jdbc:sqlite:test.sqlite", "org.sqlite.JDBC")
        db = parameters.connect()
        transaction(db) {
            SchemaUtils.create(ImageRecords)
        }
    }

    @Test
    fun `the Image table exists`() = transaction(db) {
        assertTrue {
            ImageRecords.exists()
        }
    }

    @Test
    fun `the db is empty at the start`() = transaction {
        val images = transaction(db) { ImageRecord.all().toList() }
        assertEquals(0, images.size)
    }

    @Test
    fun `an image is saved with a set UUID`() = transaction {
        val idUUID = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        val savedObject = ImageRecord.new(UUID.randomUUID()) {
            createdAt = now
            postId = idUUID
        }
        assertEquals(idUUID, savedObject.postId)
        assertEquals(now, savedObject.createdAt)
    }

    @Test
    fun `the correct image is loaded`() = transaction {
        val postId = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        ImageRecord.new(UUID.randomUUID()) {
            createdAt = now
            this.postId = postId
        }

        val loadedImage = ImageRecord.findByPostId(postId)
        assertNotNull(loadedImage)
        loadedImage as ImageRecord
        assertEquals(postId, loadedImage.postId)
        assertEquals(now, loadedImage.createdAt)
    }

    @AfterEach
    fun truncate() {
        transaction(db) {
            ImageRecords.deleteAll()
        }
    }

    @AfterAll
    fun teardown() {
        File("test.sqlite").delete()
    }


}