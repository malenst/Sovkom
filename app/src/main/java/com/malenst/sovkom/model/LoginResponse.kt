package com.malenst.sovkom.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
    val userId: Long,
    val username: String,
    val role: String
)
