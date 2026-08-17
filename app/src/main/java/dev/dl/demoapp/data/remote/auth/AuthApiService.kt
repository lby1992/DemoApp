package dev.dl.demoapp.data.remote.auth

import dev.dl.demoapp.data.remote.network.dto.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): ApiResponse<AuthResponse>
}