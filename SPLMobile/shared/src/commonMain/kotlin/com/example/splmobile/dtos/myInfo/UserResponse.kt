package com.example.splmobile.dtos.myInfo

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val data: UserSerializable,
    val status: String,
    val message: String,
)


