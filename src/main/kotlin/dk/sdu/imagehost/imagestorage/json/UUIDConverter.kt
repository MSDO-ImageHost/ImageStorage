package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import java.util.*

object UUIDConverter : Converter {
    override fun canConvert(cls: Class<*>) = cls == UUID::class.java

    override fun fromJson(jv: JsonValue): UUID = UUID.fromString(jv.string!!)

    override fun toJson(value: Any): String = (value as UUID).toString()
}