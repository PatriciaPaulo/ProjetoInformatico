package com.example.splmobile.services.userInEvent

import com.example.splmobile.objects.RequestDataResponse
import com.example.splmobile.objects.events.*
import com.example.splmobile.objects.users.UserStatsResponse
import com.example.splmobile.objects.users.UsersStatsResponse


interface UserService {
    suspend fun getUsersInEvent(user_eventId: Long, token: String): UserInEventResponse
    suspend fun getAllUsersStats(token: String): UsersStatsResponse
    suspend fun getUserStats(userID: Long,token: String): UserStatsResponse
    suspend fun postParticipateInEvent(eventId: Long, token: String): RequestDataResponse
    suspend fun patchStatusParticipateInEvent(eventId: Long, user_eventId: Long,status: String,token: String): RequestDataResponse
    suspend fun postParticipateInEventByOrganizer(eventID: Long, userID: Long, token: String): RequestDataResponse


}