package dk.sdu.imagehost.imagestorage.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

class ImageRecord(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<ImageRecord>(ImageRecords)

    var createdAt by ImageRecords.createdAt
    var owner by ImageRecords.owner

}

object ImageRecords : UUIDTable("image_record") {
    val createdAt = datetime("created_at")
    val owner = uuid("owner")
}