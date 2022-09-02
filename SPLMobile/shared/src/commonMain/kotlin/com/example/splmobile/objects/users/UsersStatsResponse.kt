package com.example.splmobile.objects.users

import kotlinx.serialization.Serializable


@Serializable
data class UsersStatsResponse(
    val data: List<UserDTO>,
    val message: String,
)