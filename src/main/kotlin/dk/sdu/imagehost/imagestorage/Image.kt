package dk.sdu.imagehost.imagestorage

import java.time.LocalDateTime
import java.util.*

data class Image(val post_id: UUID, val created_at: LocalDateTime, val image_data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as dk.sdu.imagehost.imagestorage.Image

        if (post_id != other.post_id) return false
        if (created_at != other.created_at) return false
        if (!image_data.contentEquals(other.image_data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = post_id.hashCode()
        result = 31 * result + created_at.hashCode()
        result = 31 * result + image_data.contentHashCode()
        return result
    }

}