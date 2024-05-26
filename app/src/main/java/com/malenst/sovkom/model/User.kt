package com.malenst.sovkom.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long,
    @SerializedName("username")
    val username: String,
    val password: String,
    val role: String,
    val email: String,
    val phone: String?,
    @SerializedName("coordinatorId")
    val coordinatorId: Long?,
    @SerializedName("start_coordinates")
    val startCoordinates: String?,
    @SerializedName("transport_type")
    val transportType: String?
)