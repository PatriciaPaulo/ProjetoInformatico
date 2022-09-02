package com.example.splmobile.objects.events

import com.example.splmobile.objects.equipments.EquipmentInEventDTO
import com.example.splmobile.objects.garbageSpotsInEvents.GarbageSpotInEventDTO
import com.example.splmobile.objects.garbageTypes.GarbageTypeInEventDTO
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
    val garbageTypes: List<GarbageTypeInEventDTO>,
    val equipments: List<EquipmentInEventDTO>
)