package com.example.splmobile.objects.messages

import kotlinx.serialization.Serializable



@Serializable
data class MessagesResponse(
    val data: List<MessageDTO>,
    val message: String,
)