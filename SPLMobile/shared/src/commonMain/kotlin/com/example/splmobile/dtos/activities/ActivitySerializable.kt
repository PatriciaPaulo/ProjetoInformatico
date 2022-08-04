package com.example.splmobile.dtos.activities

import kotlinx.serialization.Serializable

@Serializable
data class ActivitySerializable (
    val id: Long,
    var eventID: Long?,
    var distanceTravelled: String?,
    var steps: String?,
    var activityType: String?,
    val startDate: String?,
    val endDate: String?
    )

@Serializable
data class CreateActivitySerializable (
    var eventID: Long?,
)

@Serializable
data class ActivityTypeSerializable (
    val id: Long,
    val name: String,
    val icon: String?,
)




