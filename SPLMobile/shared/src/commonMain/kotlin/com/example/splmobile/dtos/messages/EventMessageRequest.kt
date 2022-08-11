package com.example.splmobile.dtos.messages

import kotlinx.serialization.Serializable


@Serializable
data class EventMessageRequest (
    val message:String,
    val eventID:Long,
    val type: String,
)