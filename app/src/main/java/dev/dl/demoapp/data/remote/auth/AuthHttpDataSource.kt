package dev.dl.demoapp.data.remote.auth

import dev.dl.demoapp.data.model.AuthInfo

interface AuthHttpDataSource {
    suspend fun login(
        username: String,
        password: String,
    ): AuthInfo

    suspend fun logout()
}