package com.example.splmobile.dtos.equipments

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentDTO(
    val id: Long,
    val name: String
)