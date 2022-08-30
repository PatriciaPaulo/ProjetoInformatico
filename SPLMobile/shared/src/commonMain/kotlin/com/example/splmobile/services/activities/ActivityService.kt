package com.example.splmobile.services.activities

import com.example.splmobile.objects.RequestMessageResponse
import com.example.splmobile.objects.activities.ActivitiesTypeResponse
import com.example.splmobile.objects.activities.CreateActivitySerializable

interface ActivityService {
    suspend fun postCreateActivity(activity : CreateActivitySerializable, token : String) : RequestMessageResponse
    suspend fun getActivityTypes() : ActivitiesTypeResponse
}