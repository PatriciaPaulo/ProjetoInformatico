package com.example.splmobile.dtos.garbageTypes

import kotlinx.serialization.Serializable


@Serializable
data class GarbageTypeInEventDTO(
    val id: Long,
    val eventID: Long,
    val garbageID: Long,

)