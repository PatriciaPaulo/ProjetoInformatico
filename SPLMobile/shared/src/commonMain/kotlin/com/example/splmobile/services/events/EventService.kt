package com.example.splmobile.services.events

import com.example.splmobile.objects.RequestMessageResponse
import com.example.splmobile.objects.equipments.EquipmentsResponse
import com.example.splmobile.objects.events.*


interface EventService {
    suspend fun getEvents(): EventsResponse
    suspend fun getEventsById(eventId: Long): EventResponse
    suspend fun postEvent(eventRequest: EventRequest, token: String): RequestMessageResponse
    suspend fun putEvent(eventId: Long, eventRequest: EventRequest, token: String): RequestMessageResponse
    suspend fun patchEventStatus(eventId: Long, status: String, token: String): RequestMessageResponse
    suspend fun getEquipments(token: String): EquipmentsResponse
}