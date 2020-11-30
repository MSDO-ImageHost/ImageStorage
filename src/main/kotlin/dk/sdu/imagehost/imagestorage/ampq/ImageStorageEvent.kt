package dk.sdu.imagehost.imagestorage.ampq

import com.beust.klaxon.Json
import java.time.LocalDateTime
import java.util.*

sealed class ImageStorageEvent {

    abstract val TAG: String

    sealed class Request : ImageStorageEvent() {
        data class Create(val owner: UUID, val data: ByteArray) : Request() {
            @Json(ignored = true)
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
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageLoadRequest"
        }

        data class Delete(val id: UUID) : Request() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageDeleteRequest"
        }
    }

    sealed class Response : ImageStorageEvent() {
        data class Create(val id: UUID) : Response() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageCreateResponse"
        }

        data class Load(val id: UUID, val owner: UUID, val data: ByteArray, val createdAt: LocalDateTime) :
            Response() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageLoadResponse"

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Load

                if (id != other.id) return false
                if (owner != other.owner) return false
                if (!data.contentEquals(other.data)) return false
                if (createdAt != other.createdAt) return false

                return true
            }

            override fun hashCode(): Int {
                var result = id.hashCode()
                result = 31 * result + owner.hashCode()
                result = 31 * result + data.contentHashCode()
                result = 31 * result + createdAt.hashCode()
                return result
            }
        }

        data class Delete(val id: UUID) : Response() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageDeleteResponse"
        }

        data class LoadError(val id: UUID): Response(){
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageLoadError"
        }
    }
}