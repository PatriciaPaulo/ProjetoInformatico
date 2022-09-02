package com.example.splmobile.objects.equipments

import kotlinx.serialization.Serializable

@Serializable
data class EquipmentsResponse(
    val data: List<EquipmentDTO>,
    val message: String,
)