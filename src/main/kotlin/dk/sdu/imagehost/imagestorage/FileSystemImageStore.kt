package dk.sdu.imagehost.imagestorage

import java.io.File
import java.util.*

class FileSystemImageStore(folderName: String) : ImageStore {
    private val folder = File(".", folderName).also {
        it.mkdirs()
    }
    private val extension = "png"
    private val ending = ".$extension"

    override fun listFiles(): List<UUID> =
        folder.listFiles()!!.asSequence().filter {
            it.extension.equals(extension, ignoreCase = true)
        }.map { UUID.fromString(it.nameWithoutExtension) }.toList()

    override fun save(data: ByteArray, name: UUID) {
        val file = File(folder, name.toString() + ending)
        file.writeBytes(data)
    }

    override fun delete(name: UUID) {
        val file = File(folder, name.toString() + ending)
        file.delete()
    }

    override fun exists(name: UUID): Boolean {
        val file = File(folder, name.toString() + ending)
        return file.exists()
    }

    override fun load(name: UUID): ByteArray {
        val file = File(folder, name.toString() + ending)
        return file.readBytes()
    }
}