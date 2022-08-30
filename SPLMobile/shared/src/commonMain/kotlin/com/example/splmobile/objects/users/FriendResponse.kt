package com.example.splmobile.objects.users

import kotlinx.serialization.Serializable

@Serializable
data class FriendResponse(
    val data: FriendDTO,
    val message: String,
)