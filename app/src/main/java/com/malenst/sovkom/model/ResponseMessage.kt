package com.malenst.sovkom.model

data class ResponseMessage(
    val id: Long,
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val timestamp: String,
    val readStatus: Boolean
)