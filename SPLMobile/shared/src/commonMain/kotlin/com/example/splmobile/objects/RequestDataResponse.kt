package com.example.splmobile.objects

import kotlinx.serialization.Serializable


@Serializable
data class RequestDataResponse(
    val message:String,
    val data:String,
)