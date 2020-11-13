package dk.sdu.imagehost.imagestorage.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

class Image(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<Image>(Images)

    var createdAt by Images.createdAt
    var owner by Images.owner

}

object Images : UUIDTable("image") {
    val createdAt = datetime("created_at")
    val owner = uuid("owner")
}