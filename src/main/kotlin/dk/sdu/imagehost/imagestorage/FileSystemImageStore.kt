package dk.sdu.imagehost.imagestorage

import java.io.File

class FileSystemImageStore(folderName: String) {
    val folder = File(".", folderName).also {
        it.mkdirs()
    }
    val extension = ".png"

    fun listFiles(): List<String> =
        folder.listFiles()!!.asSequence().filter {
            it.extension.equals(extension, ignoreCase = true)
        }.map { it.nameWithoutExtension }.toList()

    fun save(data: ByteArray, name: String){
        val file = File(folder, name + extension)
        file.writeBytes(data)
    }

    fun load(name: String): ByteArray {
        val file = File(folder, name + extension)
        return file.readBytes()
    }
}