package com.example.splmobile.dtos.activities

import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import kotlinx.serialization.Serializable


@Serializable
data class ActivitiesResponse(
    val data: List<ActivitySerializable>,
    val message: String,
)

@Serializable
data class ActivitiesTypeResponse(
    val data: List<ActivityTypeSerializable>,
    val message: String,
)