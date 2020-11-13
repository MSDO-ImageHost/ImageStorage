package dk.sdu.imagehost.imagestorage

import java.io.File
import java.util.*

class FileSystemImageStore(folderName: String) {
    private val folder = File(".", folderName).also {
        it.mkdirs()
    }
    private val extension = "png"
    private val ending = ".$extension"

    fun listFiles(): List<UUID> =
        folder.listFiles()!!.asSequence().filter {
            it.extension.equals(extension, ignoreCase = true)
        }.map { UUID.fromString(it.nameWithoutExtension) }.toList()

    fun save(data: ByteArray, name: UUID) {
        val file = File(folder, name.toString() + ending)
        file.writeBytes(data)
    }

    fun delete(name: UUID) {
        val file = File(folder, name.toString() + ending)
        file.delete()
    }

    fun exists(name: UUID): Boolean {
        val file = File(folder, name.toString() + ending)
        return file.exists()
    }

    fun load(name: UUID): ByteArray {
        val file = File(folder, name.toString() + ending)
        return file.readBytes()
    }
}