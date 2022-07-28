package com.example.splmobile.dtos

import kotlinx.serialization.Serializable


@Serializable
data class RequestDataResponse(
    val message:String,
    val data:String,
)