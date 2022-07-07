package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class EventsResponse(
    val data: List<EventSer>,
    val status: String,
    val message: String,
)