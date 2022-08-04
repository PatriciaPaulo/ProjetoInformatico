package com.example.splmobile.dtos.events

import com.example.splmobile.dtos.garbageSpotsInEvents.GarbageSpotInEventDTO
import com.example.splmobile.dtos.garbageTypes.GarbageTypeDTO
import com.example.splmobile.dtos.garbageTypes.GarbageTypeInEventDTO
import kotlinx.serialization.Serializable


@Serializable
data class EventDTO(
    val id: Long,
    val name: String,
    val latitude: String,
    val longitude: String,
    val status: String,
    val duration: String,
    val startDate: String,
    val description: String,
    val accessibility: String,
    val restrictions: String,
    val quantity: String,
    val observations: String?,
    val createdDate: String,
    val garbageSpots: List<GarbageSpotInEventDTO>,
    val garbageType: List<GarbageTypeInEventDTO>
)