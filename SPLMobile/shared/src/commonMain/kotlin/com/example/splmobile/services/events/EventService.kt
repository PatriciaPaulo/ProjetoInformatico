package com.example.splmobile.services.events

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.events.EventsResponse


interface EventService {
    suspend fun getEvents(): EventsResponse
    suspend fun postEvent(event: EventSerializable, token: String): RequestMessageResponse

}