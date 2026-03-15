package dev.dl.demoapp.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.dl.demoapp.data.proto.AppPrefs
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * A [Serializer] for the [AppPrefs] proto.
 */
class AppPrefsSerializer @Inject constructor() : Serializer<AppPrefs> {
    override val defaultValue: AppPrefs = AppPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AppPrefs {
        try {
            return AppPrefs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            // if the message is not initialized
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: AppPrefs,
        output: OutputStream
    ) {
        t.writeTo(output)
    }
}