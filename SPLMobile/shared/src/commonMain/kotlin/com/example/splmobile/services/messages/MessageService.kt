package com.example.splmobile.services.messages

import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageResponse
import com.example.splmobile.dtos.messages.MessagesResponse


interface MessageService {
    suspend fun getMessages(userID:Long,token: String): MessagesResponse
    suspend fun postMessage(message : IndividualMessageRequest, token: String): RequestMessageResponse
    suspend fun getLastMessage(userID: Long, token: String): MessageResponse

}