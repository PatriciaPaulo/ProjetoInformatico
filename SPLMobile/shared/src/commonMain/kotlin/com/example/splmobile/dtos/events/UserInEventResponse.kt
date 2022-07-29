package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class UserInEventResponse(
    val data: List<UserInEventDTO>,
    val message: String,
)