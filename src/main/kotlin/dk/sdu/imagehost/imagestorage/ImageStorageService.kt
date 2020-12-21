package dk.sdu.imagehost.imagestorage

import dk.sdu.imagehost.imagestorage.db.ImageRecord
import dk.sdu.imagehost.imagestorage.db.ImageRecords
import dk.sdu.imagehost.imagestorage.db.Parameters
import dk.sdu.imagehost.imagestorage.db.invoke
import org.jetbrains.exposed.sql.SchemaUtils
import java.util.*

class ImageStorageService(dbParameters: Parameters, folderName: String) {

    private val db = dbParameters.connect()

    init {
        db {
            SchemaUtils.create(ImageRecords)
        }
    }

    private val file = FileSystemImageStore(folderName)

    fun allIDs(): List<UUID> = db {
        ImageRecord.all().map { it.id.value }
    }

    fun all(): List<Image> = db {
        ImageRecord.all().toList()
    }.map {
        val id = it.id.value
        val data = file.load(id)
        val createdAt = it.createdAt
        Image(id, createdAt, data)
    }

    fun storeImage(image: Image) {
        file.save(image.image_data, image.post_id)
        db {
            val existing = ImageRecord.findById(image.post_id)
            if (existing == null) {
                ImageRecord.new(image.post_id) {
                    createdAt = image.created_at
                }
            } else {
                existing.createdAt = image.created_at
            }
        }
    }

    fun deleteImage(id: UUID) {
        file.delete(id)
        db {
            ImageRecord.findById(id)?.delete()
        }
    }

    fun exists(id: UUID): Boolean = db {
        ImageRecord.findById(id) != null
    }

    fun requestImage(imageId: UUID): Image? {
        val record = db {
            ImageRecord.findById(imageId)
        } ?: return null
        val id = record.id.value
        val data = file.load(id)
        val createdAt = record.createdAt
        return Image(id, createdAt, data)
    }

}