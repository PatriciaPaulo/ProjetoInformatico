package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class EventsResponse(
    val data: List<EventSerializable>,
    val message: String,
)