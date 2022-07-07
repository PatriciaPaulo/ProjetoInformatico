package com.example.splmobile.services.auth

import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.dtos.auth.SignInRequest
import com.example.splmobile.dtos.auth.SignInResponse

interface AuthService {
    suspend fun postLogin(loginRequest : LoginRequest) : LoginResponse
    suspend fun postSignIn(signInRequest : SignInRequest) : SignInResponse
}