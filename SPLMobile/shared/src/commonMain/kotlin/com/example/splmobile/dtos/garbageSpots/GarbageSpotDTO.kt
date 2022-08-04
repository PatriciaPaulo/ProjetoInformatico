package com.example.splmobile.dtos.garbageSpots

import com.example.splmobile.dtos.garbageSpotsInEvents.GarbageSpotInEventDTO
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