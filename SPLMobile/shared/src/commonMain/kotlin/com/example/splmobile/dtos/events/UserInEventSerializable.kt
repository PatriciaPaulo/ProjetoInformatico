package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class UserInEventSerializable(
    val id: Long,
    val event: EventSerializable,
    val userID: Long,
    val status: String,
    val creator: Boolean,

)