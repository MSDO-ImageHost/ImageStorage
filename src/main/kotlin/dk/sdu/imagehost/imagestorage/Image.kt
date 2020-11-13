package dk.sdu.imagehost.imagestorage

import org.joda.time.DateTime
import java.util.*

data class Image(val id: UUID, val owner: UUID, val createdAt: DateTime, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as dk.sdu.imagehost.imagestorage.Image

        if (id != other.id) return false
        if (owner != other.owner) return false
        if (createdAt != other.createdAt) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

}