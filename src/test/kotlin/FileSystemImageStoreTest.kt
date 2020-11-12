import dk.sdu.imagehost.imagestorage.FileSystemImageStore
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object FileSystemImageStoreTest {

    val folderName = "testStore"
    lateinit var imageStore: FileSystemImageStore
    @BeforeAll
    fun setup(){
        File(".", folderName).deleteRecursively()
        imageStore = FileSystemImageStore(folderName)
    }

    @Test
    fun `the store is empty at the start`(){
        assert(imageStore.listFiles().isEmpty())
    }

    @AfterAll
    fun cleanup(){
        File(".", folderName).deleteRecursively()
    }

}