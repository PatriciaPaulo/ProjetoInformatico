package com.example.splmobile.dtos.messages

import kotlinx.serialization.Serializable


@Serializable
data class MessageDTO (
    val id:Long,
    val message:String,
    val senderID:Long,
    val receiverID: Long,
    val deliveryDate:String,
    val status:String,
)