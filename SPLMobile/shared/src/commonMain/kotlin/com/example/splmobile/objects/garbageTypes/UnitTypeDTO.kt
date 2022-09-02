package com.example.splmobile.object.garbageTypes

import kotlinx.serialization.Serializable

@Serializable
data class UnitTypeDTO(
    val id: Long,
    val name: String
)