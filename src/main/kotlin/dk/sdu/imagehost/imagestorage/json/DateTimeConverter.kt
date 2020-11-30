package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import org.joda.time.DateTime

object DateTimeConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == DateTime::class.java

    override fun fromJson(jv: JsonValue): Any = DateTime.parse(jv.string!!)

    override fun toJson(value: Any): String = (value as DateTime).toString()
}