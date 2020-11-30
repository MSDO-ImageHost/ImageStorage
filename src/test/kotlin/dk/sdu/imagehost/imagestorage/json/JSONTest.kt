package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object JSONTest {

    lateinit var klaxon: Klaxon
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
    fun `Simple JSON Object`(){
        val instance = StringHolder("lighthouse")
        testJSON(instance)
    }

    @Test
    fun `JSON Object with a ByteArray`() {
        val data = ByteArray(255){it.toByte()}
        val instance = ByteArrayHolder("data", data)
        testJSON(instance)
    }

    @Test
    fun `JSON Object with a large ByteArray`() {
        val instance = ByteArrayHolder("lighthouse", lighthouseData)
        testJSON(instance)
    }

    @Test
    fun `JSON Object with a DateTime`(){
        val instance = DateTimeHolder("now", NOW)
        testJSON(instance)
    }

    @Test
    fun `JSON Object with an UUID`(){
        val instance = UUIDHolder("random", UUID.randomUUID())
        testJSON(instance)
    }

    inline fun <reified T> testJSON(instance: T){
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<T>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }
}