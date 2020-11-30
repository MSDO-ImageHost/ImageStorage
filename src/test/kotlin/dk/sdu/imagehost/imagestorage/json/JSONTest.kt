package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object JSONTest {

    private lateinit var klaxon: Klaxon
    private lateinit var lighthouseData: ByteArray

    val NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
    
    @BeforeAll
    fun setUp() {
        val classloader = JSONTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouseData = loadResource("lighthouse.png")

        klaxon = Klaxon().converter(Base64Converter).converter(DateTimeConverter).converter(UUIDConverter)
    }

    data class StringHolder(val tag: String)

    data class ByteArrayHolder(val tag: String, val data: ByteArray) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ByteArrayHolder

            if (tag != other.tag) return false
            if (!data.contentEquals(other.data)) return false

            return true
        }
        override fun hashCode(): Int {
            var result = tag.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }

    }

    data class DateTimeHolder(val tag: String, val datetime: LocalDateTime){
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DateTimeHolder

            if (tag != other.tag) return false
            if (datetime != other.datetime) return false

            return true
        }

        override fun hashCode(): Int {
            var result = tag.hashCode()
            result = 31 * result + datetime.hashCode()
            return result
        }
    }

    data class UUIDHolder(val tag: String, val uuid: UUID)

    @Test
    fun `Serialize and deserialize a simple JSON Object`(){
        val instance = StringHolder("lighthouse")
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<StringHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }

    @Test
    fun `Serialize and deserialize a JSON Object with a ByteArray`() {
        val data = ByteArray(255){it.toByte()}
        val instance = ByteArrayHolder("data", data)
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<ByteArrayHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }

    @Test
    fun `Serialize and deserialize a JSON Object with a large ByteArray`() {
        val instance = ByteArrayHolder("lighthouse", lighthouseData)
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<ByteArrayHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }

    @Test
    fun `Serialize and deserialize a JSON Object with a DateTime`(){
        val instance = DateTimeHolder("now", NOW)
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<DateTimeHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance.toString(), parsedInstance.toString())
    }

    @Test
    fun `Serialize and deserialize a JSON Object with an UUID`(){
        val instance = UUIDHolder("random", UUID.randomUUID())
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<UUIDHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }

}