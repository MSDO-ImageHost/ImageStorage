package dk.sdu.imagehost.imagestorage.db

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

object ParametersTest {

    @Test
    fun env(){
        val user = System.getenv("DB_USER")
        val password = System.getenv("DB_PASSWORD")
        val uri = System.getenv("DB_URI")
        val driver = System.getenv("DB_DRIVER")
        val parameters = Parameters.env()
        assertEquals(user, parameters.creds.user)
        assertEquals(password, parameters.creds.password)
        assertEquals(uri, parameters.uri)
        assertEquals(driver, parameters.driver)
    }

}