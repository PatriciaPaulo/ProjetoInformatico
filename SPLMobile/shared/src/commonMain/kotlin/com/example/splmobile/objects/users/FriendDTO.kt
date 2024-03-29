package com.example.splmobile.objects.users

import kotlinx.serialization.Serializable



@Serializable
data class FriendDTO(
    val id : Long,
    val user: UserDTO,
    val status: String,
    val date: String?,
)