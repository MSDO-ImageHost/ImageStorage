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
        val idUUID = UUID.randomUUID()
        val ownerUUID = UUID.randomUUID()
        val now = LocalDateTime.now()

        val savedObject = ImageRecord.new(idUUID) {
            createdAt = now
            owner = ownerUUID
        }
        assertEquals(idUUID, savedObject.id.value)
        assertEquals(ownerUUID, savedObject.owner)
        assertEquals(now, savedObject.createdAt)
    }

    @Test
    fun `the correct image is loaded`() = transaction {
        val idUUID = UUID.randomUUID()
        val ownerUUID = UUID.randomUUID()
        val now = LocalDateTime.now()

        ImageRecord.new(idUUID) {
            createdAt = now
            owner = ownerUUID
        }

        val loadedImage = ImageRecord.findById(idUUID)
        assertNotNull(loadedImage)
        loadedImage as ImageRecord
        assertEquals(idUUID, loadedImage.id.value)
        assertEquals(ownerUUID, loadedImage.owner)
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