package com.example.splmobile.objects.garbageSpots

import com.example.splmobile.objects.garbageSpotsInEvents.GarbageSpotInEventDTO
import kotlinx.serialization.Serializable

@Serializable
data class GarbageSpotDTO(
    val id: Long,
    var name: String,
    val creator: Long,
    var latitude: String,
    var longitude: String,
    var status: String,
    val approved: Boolean,
    val createdDate: String,
    val events: List<GarbageSpotInEventDTO>
)

@Serializable
data class GarbageSpotID(
    val id: Long,
)