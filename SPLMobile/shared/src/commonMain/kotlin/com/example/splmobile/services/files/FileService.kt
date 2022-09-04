package com.example.splmobile.services.files

import com.example.splmobile.objects.activities.ActivityID
import com.example.splmobile.objects.files.FileResponse
import com.example.splmobile.objects.files.FileSerializable
import com.example.splmobile.objects.garbageSpots.GarbageSpotID
import com.example.splmobile.objects.users.UserID


interface FileService {
    suspend fun postProfileUpload(user: UserID, fileRequest : FileSerializable, token : String) : FileResponse
    suspend fun postGarbageSpotUpload(garbageSpot: GarbageSpotID, fileRequest : FileSerializable) : FileResponse
    suspend fun postActivityUpload(activity: ActivityID, token: String, uploadFiles: Map<String, ByteArray>) : FileResponse
}