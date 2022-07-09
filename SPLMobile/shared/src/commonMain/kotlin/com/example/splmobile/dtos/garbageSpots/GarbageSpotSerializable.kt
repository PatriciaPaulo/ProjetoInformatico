package com.example.splmobile.dtos.garbageSpots

import com.example.splmobile.dtos.garbageSpotsInEvents.GarbageSpotInEventSerializable
import kotlinx.serialization.Serializable

@Serializable
data class GarbageSpotSerializable(
    val id: Long,
    var name: String,
    val creator: Long,
    var latitude: String,
    var longitude: String,
    var status: String,
    val approved: Boolean,
    val events: List<GarbageSpotInEventSerializable>

)