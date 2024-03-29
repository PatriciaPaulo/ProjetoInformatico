package com.example.splmobile.objects.equipments

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentInEventDTO(
    val id: Long,
    val eventID: Long,
    val equipmentID: Long,
    val isProvided: Boolean,
    val observations: String?
    )