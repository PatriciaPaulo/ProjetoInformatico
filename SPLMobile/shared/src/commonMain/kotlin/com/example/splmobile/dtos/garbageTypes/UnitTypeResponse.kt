package com.example.splmobile.dtos.garbageTypes

import kotlinx.serialization.Serializable

@Serializable
data class UnitTypeResponse(
    val data: List<UnitTypeDTO>,
    val message: String,
)