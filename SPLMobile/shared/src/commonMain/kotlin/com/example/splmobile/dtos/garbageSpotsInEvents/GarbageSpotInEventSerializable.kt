package com.example.splmobile.dtos.garbageSpotsInEvents

import kotlinx.serialization.Serializable


@Serializable
data class GarbageSpotInEventSerializable(
    val id: Long,
    val garbageSpotID: Long,
    val eventID: Long,
    )