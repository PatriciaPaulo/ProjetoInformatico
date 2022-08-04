package com.example.splmobile.dtos.messages

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val data: MessageDTO,
    val message: String,
)