package dk.sdu.imagehost.imagestorage.ampq

import com.beust.klaxon.Json
import java.time.LocalDateTime

sealed class ImageStorageEvent {

    abstract val TAG: String

    sealed class Request : ImageStorageEvent() {
        data class Create(val post_id: String, val image_data: ByteArray) : Request() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageCreateRequest"

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Create

                if (post_id != other.post_id) return false
                if (!image_data.contentEquals(other.image_data)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = post_id.hashCode()
                result = 31 * result + image_data.contentHashCode()
                return result
            }

        }

        data class Load(val post_id: String) : Request() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageLoadRequest"
        }

        data class Delete(val post_id: String) : Request() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageDeleteRequest"
        }
    }

    sealed class Response : ImageStorageEvent() {
        data class Create(val post_id: String) : Response() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageCreateResponse"
        }

        data class Load(val post_id: String, val image_data: ByteArray, val created_at: LocalDateTime) :
            Response() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageLoadResponse"

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Load

                if (post_id != other.post_id) return false
                if (!image_data.contentEquals(other.image_data)) return false
                if (created_at != other.created_at) return false

                return true
            }

            override fun hashCode(): Int {
                var result = post_id.hashCode()
                result = 31 * result + image_data.contentHashCode()
                result = 31 * result + created_at.hashCode()
                return result
            }

        }

        data class Delete(val post_id: String) : Response() {
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageDeleteResponse"
        }

        data class LoadError(val post_id: String): Response(){
            @Json(ignored = true)
            override val TAG: String
                get() = "ImageLoadError"
        }
    }
}