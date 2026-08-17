package dev.dl.demoapp.data.repository

import dev.dl.demoapp.data.local.datastore.AuthPreferencesDataSource
import dev.dl.demoapp.data.model.AuthInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authPrefsDataSource: AuthPreferencesDataSource,
) {
    val authInfo: Flow<AuthInfo> = authPrefsDataSource.authInfo
    val isLoggedIn: Flow<Boolean> = authInfo
        .map { it.userId.isNotBlank() && it.token.isNotBlank() }
        .distinctUntilChanged()

    suspend fun login(
        username: String,
        password: String,
    ) {

    }

    suspend fun logout() {

    }
}