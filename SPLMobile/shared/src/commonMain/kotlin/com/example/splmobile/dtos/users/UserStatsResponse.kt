package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable



@Serializable
data class UserStatsResponse(
    val data: UserSerializable,
    val message: String,
)