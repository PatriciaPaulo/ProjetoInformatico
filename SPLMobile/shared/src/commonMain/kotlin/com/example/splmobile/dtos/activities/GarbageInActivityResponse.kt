package com.example.splmobile.dtos.activities

import kotlinx.serialization.Serializable

@Serializable
data class GarbageInActivityResponse(
    val data: List<ExplicitGarbageInActivityDTO>,
    val message: String,
)

@Serializable
data class AddGarbageInActivityResponse(
    val data: GarbageInActivityDTO,
    val message: String
)