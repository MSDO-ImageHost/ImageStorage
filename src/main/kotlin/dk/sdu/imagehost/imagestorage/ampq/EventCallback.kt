package dk.sdu.imagehost.imagestorage.ampq

interface EventCallback {
    operator fun invoke(req: ImageStorageEvent.Request, res: (ImageStorageEvent.Response) -> Unit)
}