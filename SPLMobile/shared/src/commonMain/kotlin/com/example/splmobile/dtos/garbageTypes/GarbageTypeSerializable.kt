package com.example.splmobile.dtos.garbageTypes

import kotlinx.serialization.Serializable

@Serializable
data class GarbageTypeSerializable(
    val id: Long,
    val name: String
)