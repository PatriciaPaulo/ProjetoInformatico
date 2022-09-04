package com.example.splmobile.objects.garbageTypes

import kotlinx.serialization.Serializable

@Serializable
data class UnitTypeDTO(
    val id: Long,
    val name: String
)