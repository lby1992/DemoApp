package dev.dl.demoapp.data.local.datastore

import androidx.datastore.core.DataStore
import dev.dl.demoapp.data.model.AuthInfo
import dev.dl.demoapp.data.proto.AuthPrefs
import dev.dl.demoapp.data.proto.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferencesDataSource @Inject constructor(
    private val authPrefs: DataStore<AuthPrefs>,
) {
    val authInfo: Flow<AuthInfo> = authPrefs.data
        .map {
            AuthInfo(
                userId = it.userId,
                token = it.token,
            )
        }

    suspend fun updateAuthInfo(newInfo: AuthInfo) {
        authPrefs.updateData {
            it.copy {
                userId = newInfo.userId
                token = newInfo.token
            }
        }
    }
}