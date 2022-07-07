package com.example.splmobile.dtos.garbageSpots

import kotlinx.serialization.Serializable


@Serializable
data class GarbageSpotsResponse(
    val data: List<GarbageSpotSerializable>,
    val status: String,
    val message: String,
)