package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable


@Serializable
data class UsersResponse(
    val data: List<UserSerializable>,
    val message: String,
)