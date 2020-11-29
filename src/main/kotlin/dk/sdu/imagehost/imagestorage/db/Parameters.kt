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
    companion object {
        fun env(): Parameters{
            val user = System.getenv("DB_USER")
            val password = System.getenv("DB_PASSWORD")
            val uri = System.getenv("DB_URI")
            val driver = System.getenv("DB_DRIVER")
            return Parameters(Credentials(user, password), uri, driver)
        }
    }
}