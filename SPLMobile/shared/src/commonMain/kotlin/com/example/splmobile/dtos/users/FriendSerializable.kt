package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable



@Serializable
data class FriendSerializable(
    val id : Long,
    val user: UserSerializable,
    val status: String,
    val date: String,
)