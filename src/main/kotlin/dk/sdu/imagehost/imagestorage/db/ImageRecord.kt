package dk.sdu.imagehost.imagestorage.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.util.*

class ImageRecord(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<ImageRecord>(ImageRecords) {
        fun findByPostId(post_id: String): ImageRecord? = find { ImageRecords.postId eq post_id }.firstOrNull()
    }

    var postId by ImageRecords.postId
    var createdAt by ImageRecords.createdAt

}

object ImageRecords : UUIDTable("image_record") {
    val createdAt = datetime("created_at")
    val postId = varchar("post_id", 64)
}