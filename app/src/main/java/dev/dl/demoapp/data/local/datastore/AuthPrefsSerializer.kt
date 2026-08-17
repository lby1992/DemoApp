package dev.dl.demoapp.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import dev.dl.demoapp.data.proto.AuthPrefs
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * A [Serializer] for the [AuthPrefs] proto.
 */
class AuthPrefsSerializer @Inject constructor() : Serializer<AuthPrefs> {
    override val defaultValue: AuthPrefs = AuthPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AuthPrefs {
        try {
            return AuthPrefs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            // if the message is not initialized
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: AuthPrefs,
        output: OutputStream
    ) {
        t.writeTo(output)
    }
}