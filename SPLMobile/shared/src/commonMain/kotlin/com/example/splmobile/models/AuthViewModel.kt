package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.ktor.auth.authAPI
import com.example.splmobile.ktor.other.requestsAPI
import kotlinx.coroutines.async

class AuthViewModel (
    private val authAPI: authAPI,
    log: Logger

): ViewModel() {

    private val log = log.withTag("AuthViewModel")



    suspend fun postLogin(username :String,password:String): String {
        // Set loading state, which will be cleared when the repository re-emits

        var coor =  viewModelScope.async(){
            log.v { "postLogin" }
            try {
                authAPI.postLogin(username,password)
            } catch (exception: Exception) {
                handleMainError(exception)
            }
        }.await()
        return coor as String
    }
    private fun handleMainError(throwable: Throwable) {
        log.e(throwable) { "Error" }
    }
}
