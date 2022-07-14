package com.example.splmobile.services.events

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.events.EventsResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse


interface EventService {
    suspend fun getEvents(): EventsResponse
    suspend fun postEvent(event: EventSerializable, token: String): RequestMessageResponse

}