package com.example.splmobile.objects.garbageTypes

import kotlinx.serialization.Serializable

@Serializable
data class UnitTypeResponse(
    val data: List<UnitTypeDTO>,
    val message: String,
)