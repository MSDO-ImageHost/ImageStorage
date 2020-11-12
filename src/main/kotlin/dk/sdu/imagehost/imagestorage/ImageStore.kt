package dk.sdu.imagehost.imagestorage

interface ImageStore {
    fun listFiles(): List<String>
    fun save(data: ByteArray, name: String)
    fun delete(name: String)
    fun exists(name: String): Boolean
    fun load(name: String): ByteArray
}