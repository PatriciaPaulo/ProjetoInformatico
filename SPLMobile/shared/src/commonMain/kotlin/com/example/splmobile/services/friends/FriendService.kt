package com.example.splmobile.services.friends

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.EventRequest
import com.example.splmobile.dtos.events.EventResponse
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.events.EventsResponse
import com.example.splmobile.dtos.users.UserStatsResponse
import com.example.splmobile.dtos.users.UsersStatsResponse


interface FriendService {
    suspend fun getAllFriends(token: String): UsersStatsResponse
    suspend fun getFriendByID(id: Long,token: String): RequestMessageResponse
    suspend fun postFriendRequest(userID: Long, token: String): RequestMessageResponse


    // suspend fun postGarbageTypeInEvent(eventRequest: EventRequest, token: String): RequestMessageResponse

}