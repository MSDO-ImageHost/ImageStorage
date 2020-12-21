package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.db.ImageRecord
import dk.sdu.imagehost.imagestorage.db.ImageRecords
import dk.sdu.imagehost.imagestorage.db.Parameters
import dk.sdu.imagehost.imagestorage.db.invoke
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

class ImageStorageService(dbParameters: Parameters, folderName: String) {

    private val db = dbParameters.connect()

    init {
        db {
            SchemaUtils.create(ImageRecords)
        }
    }

    private val file = FileSystemImageStore(folderName)

    fun allIDs(): List<String> = db {
        ImageRecord.all().map { it.postId }
    }

    fun all(): List<Image> = db {
        ImageRecord.all().toList()
    }.map {
        val id = it.postId
        val data = file.load(id)
        val createdAt = it.createdAt
        Image(id, createdAt, data)
    }

    fun storeImage(image: Image) {
        file.save(image.image_data, image.post_id)
        db {
            val existing = ImageRecord.findByPostId(image.post_id)
            if (existing == null) {
                ImageRecord.new(UUID.randomUUID()) {
                    postId = image.post_id
                    createdAt = image.created_at
                }
            } else {
                existing.createdAt = image.created_at
            }
        }
    }

    fun deleteImage(id: String) {
        file.delete(id)
        db {
            ImageRecord.find { ImageRecords.postId eq id }.firstOrNull()?.delete()
        }
    }

    fun exists(id: String): Boolean = db {
        ImageRecord.find { ImageRecords.postId eq id }.firstOrNull() != null
    }

    fun requestImage(imageId: String): Image? {
        val record = db {
            ImageRecord.findByPostId(imageId)
        } ?: return null
        val id = record.postId
        val data = file.load(id)
        val createdAt = record.createdAt
        return Image(id, createdAt, data)
    }

}