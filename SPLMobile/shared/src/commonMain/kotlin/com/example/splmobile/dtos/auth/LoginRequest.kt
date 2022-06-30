package com.example.splmobile.dtos.auth
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest (
    val username:String,
    val password:String
)