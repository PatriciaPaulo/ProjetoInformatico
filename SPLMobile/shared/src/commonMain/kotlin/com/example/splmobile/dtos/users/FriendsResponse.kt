package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable


@Serializable
data class FriendsResponse(
    val data: List<FriendSerializable>,
    val message: String,
)