package com.example.splmobile.dtos.activities

import com.example.splmobile.dtos.garbageSpotsInEvents.GarbageSpotInEventSerializable
import kotlinx.serialization.Serializable


@Serializable
class ActivitySerializable (
    val id: Long,
    var eventID: Long,
    val userID: Long,
    var distanceTravelled: String,
    var steps: String,
    var activityType: String,
    val startDate: String,
    val endDate: String?
    )



