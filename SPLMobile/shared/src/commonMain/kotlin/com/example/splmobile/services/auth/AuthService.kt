package com.example.splmobile.services.auth

import com.example.splmobile.objects.auth.LoginRequest
import com.example.splmobile.objects.auth.LoginResponse
import com.example.splmobile.objects.auth.SignInRequest
import com.example.splmobile.objects.auth.SignInResponse

interface AuthService {
    suspend fun postLogin(loginRequest : LoginRequest) : LoginResponse
    suspend fun postSignIn(signInRequest : SignInRequest) : SignInResponse
}