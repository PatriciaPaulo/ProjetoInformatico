package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val data: EventSerializable,
    val message: String,
)