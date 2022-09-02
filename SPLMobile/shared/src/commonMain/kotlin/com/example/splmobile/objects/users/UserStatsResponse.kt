package com.example.splmobile.objects.users

import kotlinx.serialization.Serializable



@Serializable
data class UserStatsResponse(
    val data: UserDTO,
    val message: String,
)