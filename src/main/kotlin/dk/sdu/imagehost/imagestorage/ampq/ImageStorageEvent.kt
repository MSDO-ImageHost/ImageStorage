package dk.sdu.imagehost.imagestorage.ampq

import org.joda.time.DateTime
import java.util.*

sealed class ImageStorageEvent {
    sealed class Request : ImageStorageEvent() {
        data class Create(val owner: UUID, val data: ByteArray) : Request() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Create

                if (owner != other.owner) return false
                if (!data.contentEquals(other.data)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = owner.hashCode()
                result = 31 * result + data.contentHashCode()
                return result
            }
        }

        data class Load(val id: UUID) : Request()

        data class Delete(val id: UUID) : Request()
    }

    sealed class Response : ImageStorageEvent() {
        data class Create(val id: UUID) : Response()
        data class Load(val owner: UUID, val data: ByteArray, val createdAt: DateTime) :
            Response()

        data class Delete(val id: UUID) : Response()
    }
}