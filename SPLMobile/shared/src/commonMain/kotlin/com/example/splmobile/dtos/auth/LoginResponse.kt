package com.example.splmobile.dtos.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val access_token:String,
    val message:String
)