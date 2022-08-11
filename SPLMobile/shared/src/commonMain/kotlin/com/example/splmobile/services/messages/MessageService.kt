package com.example.splmobile.services.messages

import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.messages.*


interface MessageService {
    suspend fun getMessages(friendshipID:Long,token: String): MessagesResponse
    suspend fun getEventMessages(eventID: Long,token: String): MessagesResponse
    suspend fun postMessage(message : IndividualMessageRequest, token: String): RequestMessageResponse
    suspend fun postEventMessage(message : EventMessageRequest, token: String): RequestMessageResponse
    suspend fun getLastMessage(token: String): MessagesResponse
    suspend fun getLastEventMessage(token: String): MessagesResponse

}