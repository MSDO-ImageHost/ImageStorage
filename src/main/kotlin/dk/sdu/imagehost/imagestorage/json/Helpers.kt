package dk.sdu.imagehost.imagestorage.json

fun String.enquote(): String{
    return "\"$this\""
}