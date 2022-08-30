package com.example.splmobile.objects.myInfo

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val data: UserSerializable,
    val message: String,
)


