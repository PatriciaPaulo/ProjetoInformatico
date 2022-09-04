package com.example.splmobile.objects.activities

import com.example.splmobile.objects.activities.ExplicitGarbageInActivityDTO
import com.example.splmobile.objects.activities.GarbageInActivityDTO
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