package com.example.splmobile.dtos.equipments

import com.example.splmobile.dtos.garbageTypes.GarbageTypeDTO
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentsResponse(
    val data: List<EquipmentDTO>,
    val message: String,
)