package com.example.splmobile.dtos.garbageSpots

import com.example.splmobile.dtos.garbageSpotsInEvents.GarbageSpotInEventSerializable
import kotlinx.serialization.Serializable

@Serializable
data class GarbageSpotSerializable(
    val id: Long,
    var nome: String,
    val criador: Long,
    var latitude: String,
    var longitude: String,
    var estado: String,
    val aprovado: Boolean,
    val foto: String?,
    val eventos: List<GarbageSpotInEventSerializable>
)