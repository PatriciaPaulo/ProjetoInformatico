package com.example.splmobile.dtos.events

import com.example.splmobile.dtos.equipments.EquipmentInEventDTO
import kotlinx.serialization.Serializable


@Serializable
data class EventRequest(
    val event: EventDTO,
    val garbageTypeList: List<Long>,
    val garbageSpotList: List<Long>,
    val equipmentList: List<EquipmentInEventDTO>,
)