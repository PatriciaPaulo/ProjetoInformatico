package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.services.auth.AuthService
import kotlinx.coroutines.async

class AuthViewModel (
    private val authService: AuthService,
    log: Logger

): ViewModel() {
    val token = mutableListOf<String>()
    private val log = log.withTag("AuthViewModel")



     suspend fun postLogin(username :String, password:String): LoginResponse {

        // Set loading state, which will be cleared when the repository re-emits
        var loginResponse : LoginResponse = viewModelScope.async(){
            log.v { "postLogin" }
            try {
                authService.postLogin(LoginRequest(username,password))
            } catch (exception: Exception) {
                handleMainError(exception)
            }
        }.await() as LoginResponse
         if(loginResponse.message == "success"){
             token.add(loginResponse.access_token)
         }
        return loginResponse


    }
    private fun handleMainError(throwable: Throwable) {
        log.e(throwable) { "Error" }
    }
}
