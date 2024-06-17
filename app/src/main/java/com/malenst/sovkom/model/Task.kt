package com.malenst.sovkom.model

import java.time.LocalDateTime

data class Task(
    val id: Long,
    val description: String,
    val departureCoordinates: String?,
    val destinationCoordinates: String,
    val status: String,
    val assignmentDate: String,
    val startTime: String,
    val endTime: String,
    val coordinatorId: Long?,
    val executorId: Long?,
    val category: String
)