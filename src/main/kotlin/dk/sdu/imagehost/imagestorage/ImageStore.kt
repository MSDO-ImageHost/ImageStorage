package dk.sdu.imagehost.imagestorage

import java.util.*

interface ImageStore {
    fun listFiles(): List<UUID>
    fun save(data: ByteArray, name: UUID)
    fun delete(name: UUID)
    fun exists(name: UUID): Boolean
    fun load(name: UUID): ByteArray
}