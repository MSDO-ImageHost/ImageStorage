package dk.sdu.imagehost.imagestorage


fun main(args: Array<String>) {
    println("starting ImageStorage!")

    val rc = Receive("Gateway")
    rc.receiver(mutableListOf("storeImage", "requestImage", "deleteImage"))
    println("Gateway receiver running")


}