package dev.dl.demoapp.data.remote.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val userId: String,
    val token: String,
)