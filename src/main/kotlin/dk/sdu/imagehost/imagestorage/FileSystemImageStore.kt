package dk.sdu.imagehost.imagestorage

import java.io.File
import java.util.*

class FileSystemImageStore(folderName: String) {
    private val folder = File(".", folderName).also {
        it.mkdirs()
    }
    private val extension = "png"
    private val ending = ".$extension"

    fun listFiles(): List<String> =
        folder.listFiles()!!.asSequence().filter {
            it.extension.equals(extension, ignoreCase = true)
        }.map { it.nameWithoutExtension }.toList()

    fun save(data: ByteArray, name: String) {
        val file = File(folder, name + ending)
        file.writeBytes(data)
    }

    fun delete(name: String) {
        val file = File(folder, name + ending)
        file.delete()
    }

    fun exists(name: String): Boolean {
        val file = File(folder, name + ending)
        return file.exists()
    }

    fun load(name: String): ByteArray {
        val file = File(folder, name + ending)
        return file.readBytes()
    }
}