package com.example.splmobile.services.files

import com.example.splmobile.dtos.activities.ActivityID
import com.example.splmobile.dtos.files.FileResponse
import com.example.splmobile.dtos.files.FileSerializable
import com.example.splmobile.dtos.garbageSpots.GarbageSpotID
import com.example.splmobile.dtos.users.UserID

interface FileService {
    suspend fun postProfileUpload(user: UserID, fileRequest : FileSerializable, token : String) : FileResponse
    suspend fun postGarbageSpotUpload(garbageSpot: GarbageSpotID, fileRequest : FileSerializable) : FileResponse
    suspend fun postActivityUpload(activity: ActivityID, file : Unit, token : String) : FileResponse
}