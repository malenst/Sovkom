package com.malenst.sovkom.model

import java.time.LocalDateTime

data class Task(
    val id: Long,
    val description: String,
    val departureCoordinates: String?,
    val destinationCoordinates: String,
    val status: String,
    val assignmentDate: String,  // Используйте String для упрощения, предполагая что используется ISO формат
    val startTime: String,       // То же
    val endTime: String,
    val coordinatorId: Long?,
    val executorId: Long?,
    val category: String
)