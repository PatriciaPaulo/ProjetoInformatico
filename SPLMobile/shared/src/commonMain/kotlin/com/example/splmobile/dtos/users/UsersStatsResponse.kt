package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable


@Serializable
data class UsersStatsResponse(
    val data: List<UserDTO>,
    val message: String,
)