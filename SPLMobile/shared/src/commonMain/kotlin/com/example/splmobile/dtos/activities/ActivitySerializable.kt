package com.example.splmobile.dtos.activities

import kotlinx.serialization.Serializable

@Serializable
class ActivitySerializable (
    val id: Long,
    var eventID: Long?,
    var distanceTravelled: String?,
    var steps: String?,
    var activityType: String?,
    val startDate: String?,
    val endDate: String?
    )

@Serializable
class CreateActivitySerializable (
    var eventID: Long?,
)





