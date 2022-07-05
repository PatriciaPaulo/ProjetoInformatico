package com.example.splmobile.dtos.auth

import com.example.splmobile.dtos.RequestMessageResponse
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val access_token:String,
    val message: String,
    val status: String,
)