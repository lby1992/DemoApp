package dev.dl.demoapp.data.remote.network.dto

import kotlinx.serialization.Serializable

/*
{
  "result": "10000",
  "message": "ok",
  "data": { ... }
}
 */
@Serializable
data class ApiResponse<T>(
    val result: String,
    val message: String?,
    val data: T?,
)