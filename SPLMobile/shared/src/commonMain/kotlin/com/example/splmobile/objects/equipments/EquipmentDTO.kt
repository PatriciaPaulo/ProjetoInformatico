package com.example.splmobile.objects.equipments

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDTO(
    val id: Long,
    val name: String
)