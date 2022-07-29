package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class EventRequest(
    val event: EventDTO,
    val garbageList: List<Long>,
)