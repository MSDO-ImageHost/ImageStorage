package dk.sdu.imagehost.imagestorage.json

import com.beust.klaxon.Klaxon
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object Base64ConverterTest {

    private lateinit var klaxon: Klaxon
    private lateinit var lighthouseData: ByteArray

    @BeforeAll
    fun setUp() {
        val classloader = Base64ConverterTest::class.java.classLoader
        fun loadResource(string: String) = classloader.getResource(string)!!.readBytes()
        lighthouseData = loadResource("lighthouse.png")

        klaxon = Klaxon().converter(Base64Converter).converter(DateTimeConverter).converter(UUIDConverter)
    }

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

    data class StringHolder(val tag: String)

    @Test
    fun `Serialize and deserialize a simple JSON Object`(){
        val instance = StringHolder("lighthouse")
        val encoded = klaxon.toJsonString(instance)
        val parsedInstance = klaxon.parse<StringHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }

    @Test
    fun serializeDeserialize() {
        val data = ByteArray(255){it.toByte()}
        val instance = ByteArrayHolder("lighthouse", data)
        val encoded = klaxon.toJsonString(instance)
        println(encoded)
        val parsedInstance = klaxon.parse<ByteArrayHolder>(encoded)
        assertNotNull(parsedInstance)
        assertEquals(instance, parsedInstance)
    }

}