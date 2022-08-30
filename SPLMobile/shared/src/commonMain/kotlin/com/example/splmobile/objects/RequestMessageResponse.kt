package com.example.splmobile.objects

import kotlinx.serialization.Serializable


@Serializable
data class RequestMessageResponse(
    val message:String,
)