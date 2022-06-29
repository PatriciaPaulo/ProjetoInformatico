package com.example.splmobile.dtos.auth

import kotlinx.serialization.Serializable


@Serializable
data class SignInResponse(
    val access_token:String,
    val message: String
)