package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import java.util.*

object Base64Converter : Converter {

    override fun canConvert(cls: Class<*>): Boolean = cls == ByteArray::class.java

    override fun fromJson(jv: JsonValue): ByteArray = Base64.getDecoder().decode(jv.string!!)

    override fun toJson(value: Any): String = Base64.getEncoder().encodeToString(value as ByteArray).enquote()

}