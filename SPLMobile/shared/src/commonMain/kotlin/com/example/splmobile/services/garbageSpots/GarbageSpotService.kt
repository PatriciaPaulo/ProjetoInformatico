package com.example.splmobile.services.garbageSpots

import GarbageSpotResponse
import com.example.splmobile.objects.RequestMessageResponse
import com.example.splmobile.objects.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.objects.garbageSpots.GarbageSpotDTO
import com.example.splmobile.objects.garbageTypes.GarbageTypesResponse
import com.example.splmobile.objects.garbageTypes.UnitTypeResponse


interface GarbageSpotService {
      suspend fun getGarbageSpots(token: String): GarbageSpotsResponse
      suspend fun postGarbageSpot(garbageSpot: GarbageSpotDTO, token: String): RequestMessageResponse
      suspend fun patchGarbageSpotStatus(garbageSpotID: Long, status: String, token: String): RequestMessageResponse
      suspend fun getGarbageTypes(token: String): GarbageTypesResponse
      suspend fun getUnitTypes(): UnitTypeResponse
      suspend fun getGarbageSpotById(gsId: Long, token: String): GarbageSpotResponse
}