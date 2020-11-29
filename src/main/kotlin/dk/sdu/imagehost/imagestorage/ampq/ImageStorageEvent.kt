package dk.sdu.imagehost.imagestorage.ampq

import org.joda.time.DateTime
import java.util.*

sealed class ImageStorageEvent {
    sealed class ImageStorageRequest : ImageStorageEvent() {
        data class ImageCreateRequest(val owner: UUID, val data: ByteArray) : ImageStorageRequest() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ImageCreateRequest

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

        data class ImageLoadRequest(val id: UUID) : ImageStorageRequest()

        data class ImageDeleteRequest(val id: UUID) : ImageStorageRequest()
    }

    sealed class ImageStorageResponse : ImageStorageEvent() {
        data class ImageCreateResponse(val id: UUID) : ImageStorageResponse()
        data class ImageLoadRequest(val owner: UUID, val data: ByteArray, val createdAt: DateTime) :
            ImageStorageResponse()

        data class ImageDeleteRequest(val id: UUID) : ImageStorageResponse()
    }
}