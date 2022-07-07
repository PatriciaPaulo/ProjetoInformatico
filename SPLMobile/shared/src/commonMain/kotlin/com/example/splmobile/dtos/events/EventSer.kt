package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class EventSer(
    val id: Long,
    val name: String,
    val latitude: String,
    val longitude: String,
    val organizer: Long,
    val status: String,
    val duration: String,
    val startingDate: String,
    val description: String,
    val acessibility: String,
    val restrictions: String,
    val garbageType: String,
    val quantity: String,
    val photo: String?,
    val comments: String?

)