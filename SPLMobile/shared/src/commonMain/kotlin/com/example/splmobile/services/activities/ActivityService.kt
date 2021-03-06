package com.example.splmobile.services.activities

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.activities.ActivitySerializable
import com.example.splmobile.dtos.activities.CreateActivitySerializable

interface ActivityService {
    suspend fun postCreateActivity(activity : CreateActivitySerializable, token : String) : RequestMessageResponse
}