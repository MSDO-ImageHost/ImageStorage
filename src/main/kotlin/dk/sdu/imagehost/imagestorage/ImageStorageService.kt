package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.db.Parameters
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ImageStorageService(dbParameters: Parameters) {

    private val db = dbParameters.connect()

    fun listFiles(): List<Image> {
        TODO("Not yet implemented")
    }

    fun save(image: Image) {
        TODO("Not yet implemented")
    }

    fun delete(id: UUID) {
        TODO("Not yet implemented")
    }

    fun exists(id: UUID): Boolean {
        TODO("Not yet implemented")
    }

    fun load(id: UUID): ByteArray {
        TODO("Not yet implemented")
    }

}

private operator fun <T> Database.invoke(function: () -> T): T {
    return transaction {
        function()
    }
}

