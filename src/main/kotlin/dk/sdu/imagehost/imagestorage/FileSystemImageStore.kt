package dk.sdu.imagehost.imagestorage

import java.io.File

class FileSystemImageStore(folderName: String) : ImageStore {
    private val folder = File(".", folderName).also {
        it.mkdirs()
    }
    private val extension = "png"
    private val ending = ".$extension"

    override fun listFiles(): List<String> =
        folder.listFiles()!!.asSequence().filter {
            it.extension.equals(extension, ignoreCase = true)
        }.map { it.nameWithoutExtension }.toList()

    override fun save(data: ByteArray, name: String) {
        val file = File(folder, name + ending)
        file.writeBytes(data)
    }

    override fun delete(name: String) {
        val file = File(folder, name + ending)
        file.delete()
    }

    override fun exists(name: String): Boolean {
        val file = File(folder, name + ending)
        return file.exists()
    }

    override fun load(name: String): ByteArray {
        val file = File(folder, name + ending)
        return file.readBytes()
    }
}