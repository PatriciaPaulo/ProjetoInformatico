package com.example.splmobile.services.activities

import com.example.splmobile.objects.activities.*
import com.example.splmobile.objects.messages.MessagesResponse

interface ActivityService {
    suspend fun postCreateActivity(activity : CreateActivitySerializable, token : String) : CreateActivityResponse
    suspend fun getActivityTypes() : ActivitiesTypeResponse
    suspend fun getGarbageInActivity(currentActivity : Int, token: String) : GarbageInActivityResponse
    suspend fun patchGarbageInActivity(currentActivity: Long, garbageInActivityID : Long, garbage: GarbageAmountDTO, token: String) : MessagesResponse
    suspend fun postGarbageInActivity(garbage: GarbageInActivityDTO, token: String, activityID: Long) : AddGarbageInActivityResponse
    suspend fun deleteGarbageInActivity(garbageToDelete: Long, token: String): MessagesResponse
}