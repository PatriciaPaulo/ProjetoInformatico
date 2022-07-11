package com.example.splmobile.dtos.garbageTypes

import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import kotlinx.serialization.Serializable


@Serializable
data class GarbageTypesResponse(
    val data: List<GarbageTypesSerializable>,
    val message: String,
)