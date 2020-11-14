package dk.sdu.imagehost.imagestorage.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

operator fun <T> Database.invoke(function: () -> T): T {
    return transaction {
        function()
    }
}