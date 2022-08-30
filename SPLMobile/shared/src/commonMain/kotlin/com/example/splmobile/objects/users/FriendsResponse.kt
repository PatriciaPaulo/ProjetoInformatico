package com.example.splmobile.objects.users

import kotlinx.serialization.Serializable


@Serializable
data class FriendsResponse(
    val data: List<FriendDTO>,
    val message: String,
)