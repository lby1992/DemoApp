package dev.dl.demoapp.data.local.datastore

import androidx.datastore.core.CorruptionException
import dev.dl.demoapp.data.proto.appPrefs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AppPrefsSerializerTest {
    private val serializer = AppPrefsSerializer()

    @Test
    fun `default app prefs is empty`() {
        val default = serializer.defaultValue
        assertEquals(appPrefs {
            // Defaults
        }, default)
    }

    @Test
    fun `writing and reading app prefs that outputs correct value`() = runTest {
        val expectedAppPrefs = appPrefs {
            lastOnboardingVersion = 10
            useDynamicColor = true
        }
        val outputStream = ByteArrayOutputStream()
        expectedAppPrefs.writeTo(outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val actualAppPrefs = serializer.readFrom(inputStream)
        assertEquals(expectedAppPrefs, actualAppPrefs)
    }

    @Test(expected = CorruptionException::class)
    fun `reading from invalid input stream causes CorruptionException`() = runTest {
        val invalidInputStream = ByteArrayInputStream(byteArrayOf(0x0, 0x1))
        serializer.readFrom(invalidInputStream)
    }
}