package com.example.splmobile.objects.activities

import kotlinx.serialization.Serializable

@Serializable
data class ActivitySerializable(
    val id: Long,
    var eventID: Long?,
    var distanceTravelled: String?,
    var activityTypeID: Long?,
    val startDate: String?,
    val endDate: String?,
)

@Serializable
data class PatchActivitySerializable(
    val id: Long,
    var distanceTravelled: String?,
    val activityTypeID: Long?,
    val endDate: String?
)

@Serializable
data class ActivityID (
    val id: Long,
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




