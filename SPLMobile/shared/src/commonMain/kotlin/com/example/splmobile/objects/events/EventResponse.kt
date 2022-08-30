package com.example.splmobile.objects.events

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val data: EventDTO,
    val message: String,
)