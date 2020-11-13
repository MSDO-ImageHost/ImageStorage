import dk.sdu.imagehost.imagestorage.ImageStorageDB
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object ImageStorageDBTest {

    lateinit var db: ImageStorageDB

    @BeforeAll
    fun setup(){
        val credentials = ImageStorageDB.Credentials("", "")
        val parameters = ImageStorageDB.Parameters(credentials, "jdbc:h2:mem:test", "org.h2.Driver")
        db = ImageStorageDB(parameters)
    }
}