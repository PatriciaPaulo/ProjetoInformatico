package com.example.splmobile.dtos

import kotlinx.serialization.Serializable


@Serializable
data class RequestMessageResponse(
    val message:String,
    val status:String,
)