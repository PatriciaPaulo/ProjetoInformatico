package com.example.splmobile.objects.events

import kotlinx.serialization.Serializable


@Serializable
data class UserInEventResponse(
    val data: List<UserInEventDTO>,
    val message: String,
)