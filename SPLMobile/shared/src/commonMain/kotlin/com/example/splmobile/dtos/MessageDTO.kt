package com.example.splmobile.dtos

import kotlinx.serialization.Serializable


@Serializable
data class MessageDTO (
    val text:String,
    val senderID:Long,
    val id:Long,
    val date:String
)