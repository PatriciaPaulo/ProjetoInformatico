package com.example.splmobile.services.events

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.EventsResponse
import com.example.splmobile.dtos.events.EventRequest
import com.example.splmobile.dtos.events.EventResponse
import com.example.splmobile.dtos.events.EventSerializable


interface EventService {
    suspend fun getEvents(): EventsResponse
    suspend fun getEventsById(eventId: Long): EventResponse
    suspend fun postEvent(eventRequest: EventRequest, token: String): RequestMessageResponse
    suspend fun postParticipateInEvent(eventId: Long, token: String): RequestMessageResponse
    suspend fun patchStatusParticipateInEvent(eventId: Long, user_eventId: Long,status: String,token: String): RequestMessageResponse
    suspend fun putEvent(eventId: Long, event: EventSerializable, token: String): RequestMessageResponse
    suspend fun patchEventStatus(eventId: Long, status: String, token: String): RequestMessageResponse

    // suspend fun postGarbageTypeInEvent(eventRequest: EventRequest, token: String): RequestMessageResponse

}