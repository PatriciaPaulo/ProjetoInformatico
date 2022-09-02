package com.example.splmobile.objects.auth
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest (
    val email : String,
    val password : String,
    val passwordConfirmation : String,
)