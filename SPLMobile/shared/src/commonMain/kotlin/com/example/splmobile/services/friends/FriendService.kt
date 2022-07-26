package com.example.splmobile.services.friends

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.EventRequest
import com.example.splmobile.dtos.events.EventResponse
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.events.EventsResponse



interface FriendService {
    suspend fun getAllFriends(): RequestMessageResponse
    suspend fun getFriendByID(id: Long): RequestMessageResponse
    suspend fun postFriendRequest(userID: Long, token: String): RequestMessageResponse


    // suspend fun postGarbageTypeInEvent(eventRequest: EventRequest, token: String): RequestMessageResponse

}