package com.example.splmobile.dtos.garbageTypes

import kotlinx.serialization.Serializable



@Serializable
data class GarbageTypesSerializable(
    val id: Long,
    val name: String,
    val isSelected: Boolean? = false
)