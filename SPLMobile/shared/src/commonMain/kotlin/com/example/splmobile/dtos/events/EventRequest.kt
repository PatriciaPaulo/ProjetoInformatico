package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class EventRequest(
    val event: EventDTO,
    val garbageTypeList: List<Long>,
    val garbageSpotList: List<Long>,
)