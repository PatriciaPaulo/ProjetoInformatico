package com.example.splmobile.objects.garbageTypes

import kotlinx.serialization.Serializable

@Serializable
data class GarbageTypeDTO(
    val id: Long,
    val name: String
)