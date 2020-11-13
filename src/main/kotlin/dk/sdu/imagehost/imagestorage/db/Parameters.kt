package dk.sdu.imagehost.imagestorage.db

import org.jetbrains.exposed.sql.Database

data class Parameters(val creds: Credentials, val uri: String, val driver: String){
    fun connect(): Database {
        return Database.connect(
            uri,
            driver,
            creds.user,
            creds.password
        )
    }
}