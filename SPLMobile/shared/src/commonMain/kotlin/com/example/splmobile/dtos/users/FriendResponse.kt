package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable

@Serializable
data class FriendResponse(
    val data: FriendDTO,
    val message: String,
)