package com.example.splmobile.services.activities

import com.example.splmobile.objects.RequestMessageResponse
import com.example.splmobile.objects.activities.*
import com.example.splmobile.objects.messages.MessagesResponse

interface ActivityService {
    suspend fun postCreateActivity(activity : CreateActivitySerializable, token : String) : ActivityResponse
    suspend fun patchActivity(activity : PatchActivitySerializable, token: String) : RequestMessageResponse
    suspend fun getActivityTypes() : ActivitiesTypeResponse
    suspend fun getGarbageInActivity(currentActivity : Long, token: String) : GarbageInActivityResponse
    suspend fun patchGarbageInActivity(currentActivity: Long, garbageInActivityID : Long, garbage: GarbageAmountDTO, token: String) : MessagesResponse
    suspend fun postGarbageInActivity(garbage: GarbageInActivityDTO, token: String, activityID: Long) : AddGarbageInActivityResponse
    suspend fun deleteGarbageInActivity(garbageToDelete: Long, token: String): MessagesResponse
    suspend fun getLastActivity(token: String) : ActivityResponse
}