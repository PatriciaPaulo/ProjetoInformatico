package com.example.splmobile.objects.messages

import kotlinx.serialization.Serializable


@Serializable
data class EventMessageRequest (
    val message:String,
    val eventID:Long,
    val type: String,
)