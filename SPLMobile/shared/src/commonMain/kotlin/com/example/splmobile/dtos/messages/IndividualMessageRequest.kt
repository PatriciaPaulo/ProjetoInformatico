package com.example.splmobile.dtos.messages

import kotlinx.serialization.Serializable


@Serializable
data class IndividualMessageRequest (
    val message:String,
    val userID:Long,
    val type: String,
)