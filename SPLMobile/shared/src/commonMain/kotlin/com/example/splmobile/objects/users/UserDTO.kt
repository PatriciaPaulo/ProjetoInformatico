package com.example.splmobile.objects.users

import kotlinx.serialization.Serializable



@Serializable
data class UserDTO(
    val id: Long,
    val username: String,
    val name: String,
    val events_participated: String,
    val activities_completed: String,
    val garbage_spots_created: String,
)

@Serializable
data class UserID(
    val id: Long,
)