package com.example.splmobile.objects.messages

import kotlinx.serialization.Serializable


@Serializable
data class MessageDTO (
    val id:Long,
    val message:String,
    val senderID:Long,
    // if individual message receiverID -> userID
    // if event message receiverID ->eventID
    val receiverID: Long,
    val deliveryDate:String?,
    val status:String,
)