package dk.sdu.imagehost.imagestorage.ampq

import org.joda.time.DateTime
import java.util.*

sealed class ImageStorageEvent {

    abstract val TAG: String

    sealed class Request : ImageStorageEvent() {
        data class Create(val owner: UUID, val data: ByteArray) : Request() {
            override val TAG: String
                get() = "ImageCreateRequest"

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

        data class Load(val id: UUID) : Request() {
            override val TAG: String
                get() = "ImageLoadRequest"
        }

        data class Delete(val id: UUID) : Request() {
            override val TAG: String
                get() = "ImageDeleteRequest"
        }
    }

    sealed class Response : ImageStorageEvent() {
        data class Create(val id: UUID) : Response() {
            override val TAG: String
                get() = "ImageCreateResponse"
        }

        data class Load(val owner: UUID, val data: ByteArray, val createdAt: DateTime) :
            Response() {
            override val TAG: String
                get() = "ImageLoadResponse"
        }

        data class Delete(val id: UUID) : Response() {
            override val TAG: String
                get() = "ImageDeleteResponse"
        }
    }
}