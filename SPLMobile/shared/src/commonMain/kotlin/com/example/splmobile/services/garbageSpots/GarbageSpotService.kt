package com.example.splmobile.services.garbageSpots

import GarbageSpotResponse
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse


interface GarbageSpotService {
      suspend fun getGarbageSpots(token: String): GarbageSpotsResponse
      suspend fun postGarbageSpot(garbageSpot: GarbageSpotDTO, token: String): RequestMessageResponse
      suspend fun patchGarbageSpotStatus(garbageSpotID: Long, status: String, token: String): RequestMessageResponse
      suspend fun getGarbageTypes(token: String): GarbageTypesResponse
      suspend fun getGarbageSpotById(gsId: Long, token: String): GarbageSpotResponse
      suspend fun postGarbageSpotsInEvent(eventID: Long, token: String): RequestMessageResponse
}