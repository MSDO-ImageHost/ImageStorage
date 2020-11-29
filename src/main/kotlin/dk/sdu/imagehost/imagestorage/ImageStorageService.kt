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
        val owner = it.owner
        val createdAt = it.createdAt
        Image(id, owner, createdAt, data)
    }

    fun storeImage(image: Image) {
        file.save(image.data, image.id)
        db {
            val existing = ImageRecord.findById(image.id)
            if (existing == null) {
                ImageRecord.new(image.id) {
                    owner = image.owner
                    createdAt = image.createdAt
                }
            } else {
                existing.owner = image.owner
                existing.createdAt = image.createdAt
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
        val owner = record.owner
        val createdAt = record.createdAt
        return Image(id, owner, createdAt, data)
    }

}