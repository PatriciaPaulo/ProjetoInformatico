package com.example.splmobile.objects.auth

import kotlinx.serialization.Serializable


@Serializable
data class SignInResponse(
    val access_token:String,
    val message: String,
    val status: Int
)