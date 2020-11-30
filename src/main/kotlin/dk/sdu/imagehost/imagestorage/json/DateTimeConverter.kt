package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import java.time.LocalDateTime

object DateTimeConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == LocalDateTime::class.java

    override fun fromJson(jv: JsonValue): Any = LocalDateTime.parse(jv.string!!)

    override fun toJson(value: Any): String = (value as LocalDateTime).toString().enquote()
}