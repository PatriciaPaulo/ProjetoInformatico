package com.example.splmobile.services.garbageSpots

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse


interface GarbageSpotService {
      suspend fun getGarbageSpots(): GarbageSpotsResponse
      suspend fun postGarbageSpot(garbageSpot: GarbageSpotSerializable, token: String): RequestMessageResponse
      suspend fun patchGarbageSpotStatus(garbageSpot: GarbageSpotSerializable, status: String, token: String): RequestMessageResponse
      suspend fun getGarbageTypes(token: String): GarbageTypesResponse
}