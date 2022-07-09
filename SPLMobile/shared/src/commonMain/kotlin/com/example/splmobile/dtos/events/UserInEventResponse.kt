package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class UserInEventResponse(
    val data: List<UserInEventSerializable>,
    val message: String,
)