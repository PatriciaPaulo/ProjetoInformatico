package com.example.splmobile.objects.events

import kotlinx.serialization.Serializable


@Serializable
data class UserInEventDTO(
    val id: Long,
    val event: EventDTO,
    val userID: Long,
    val status: String,
    val creator: Boolean,
    val enteringDate :String
    )