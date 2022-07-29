package com.example.splmobile.services.events

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.*


interface EventService {
    suspend fun getEvents(): EventsResponse
    suspend fun getEventsById(eventId: Long): EventResponse
    suspend fun postEvent(eventRequest: EventRequest, token: String): RequestMessageResponse
    suspend fun putEvent(eventId: Long, event: EventDTO, token: String): RequestMessageResponse
    suspend fun patchEventStatus(eventId: Long, status: String, token: String): RequestMessageResponse


    // suspend fun postGarbageTypeInEvent(eventRequest: EventRequest, token: String): RequestMessageResponse

}