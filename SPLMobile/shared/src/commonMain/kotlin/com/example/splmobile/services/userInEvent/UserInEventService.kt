package com.example.splmobile.services.userInEvent

import com.example.splmobile.dtos.RequestDataResponse
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.*
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse
import com.example.splmobile.dtos.users.UsersResponse


interface UserInEventService {
    suspend fun getUsersInEvent(user_eventId: Long, token: String): UserInEventResponse
    suspend fun getAllUsersStats(token: String): UsersResponse
    suspend fun postUserInEvent(user_eventID: Long, userID: Long): RequestMessageResponse
    suspend fun postParticipateInEvent(eventId: Long, token: String): RequestDataResponse
    suspend fun patchStatusParticipateInEvent(eventId: Long, user_eventId: Long,status: String,token: String): RequestDataResponse
    suspend fun postParticipateInEventByOrganizer(eventID: Long, userID: Long, token: String): RequestDataResponse

}