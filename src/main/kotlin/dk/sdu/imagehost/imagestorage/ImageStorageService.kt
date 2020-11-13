package dk.sdu.imagehost.imagestorage

import java.util.*

class ImageStorageService : ImageStore {
    override fun listFiles(): List<UUID> {
        TODO("Not yet implemented")
    }

    override fun save(data: ByteArray, name: UUID) {
        TODO("Not yet implemented")
    }

    override fun delete(name: UUID) {
        TODO("Not yet implemented")
    }

    override fun exists(name: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun load(name: UUID): ByteArray {
        TODO("Not yet implemented")
    }

}

