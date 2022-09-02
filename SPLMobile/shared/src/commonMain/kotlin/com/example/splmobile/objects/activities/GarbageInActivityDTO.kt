package com.example.splmobile.dtos.activities

import kotlinx.serialization.Serializable

@Serializable
data class GarbageInActivityDTO(
    val id: Long,
    val activityID: Long,
    val garbageID: Long,
    val amount: Float,
    val unitTypeID: Long,
)

@Serializable
data class GarbageAmountDTO(
    val amount: Float
)

@Serializable
data class ExplicitGarbageInActivityDTO(
    val id: Long,
    val garbage: String,
    val amount: Float,
    val unit: String,
)