package com.example.splmobile.dtos.events

import com.example.splmobile.dtos.events.EventSerializable
import kotlinx.serialization.Serializable


@Serializable
data class EventRequest(
    val event: EventSerializable,
    val garbageList: List<Long>,
)