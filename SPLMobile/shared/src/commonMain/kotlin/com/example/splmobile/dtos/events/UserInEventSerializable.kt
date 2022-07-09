package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class UserInEventSerializable(
    val id: Long,
    val event: List<EventSerializable>,
    val userID: Long,
    val status: String,

)