package com.example.splmobile.dtos.messages

import com.example.splmobile.dtos.garbageTypes.GarbageTypeDTO
import kotlinx.serialization.Serializable



@Serializable
data class MessagesResponse(
    val data: List<MessageDTO>,
    val message: String,
)