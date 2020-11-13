package dk.sdu.imagehost.imagestorage

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

class ImageStorageDB(parameters: Parameters) {

    data class Credentials(val user: String, val password: String)
    data class Parameters(val creds: Credentials, val uri: String, val driver: String)

    object Images : UUIDTable() {
        val uploadedAt = datetime("uploaded_at")
        val owner = uuid("owner")
    }

    class Image(id: EntityID<UUID>) : UUIDEntity(id) {
        companion object : UUIDEntityClass<Image>(Images)

        var uploadedAt by Images.uploadedAt
        var owner by Images.owner
    }

    val connection = parameters.connect()

    companion object {
        private fun Parameters.connect(): Database {
            return Database.connect(
                uri,
                driver,
                creds.user,
                creds.password
            )
        }
    }
}