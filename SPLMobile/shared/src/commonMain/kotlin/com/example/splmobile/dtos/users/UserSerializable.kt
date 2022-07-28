package com.example.splmobile.dtos.users

import kotlinx.serialization.Serializable



@Serializable
data class UserSerializable(
    val id: Long,
    val username: String,
    val name: String,
    val events_participated: String,
    val activities_completed: String,
    val garbage_spots_created: String,
)