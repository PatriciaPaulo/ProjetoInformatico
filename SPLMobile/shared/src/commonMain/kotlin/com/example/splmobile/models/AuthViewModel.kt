package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.models.locaisLixo.LocalLixoViewState
import com.example.splmobile.services.auth.AuthService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel (
    private val authService: AuthService,
    log: Logger

): ViewModel() {
    private val _loginUIState = MutableStateFlow<LoginUIState>(LoginUIState.Empty)
    val loginUIState: StateFlow<LoginUIState> = _loginUIState



    sealed class LoginUIState{
        object Success :LoginUIState()
        data class Error(val message: String):LoginUIState()
        object Loading : LoginUIState()
        object Empty : LoginUIState()
    }

    private val mutableTokenState: MutableStateFlow<String> = MutableStateFlow("")
    val tokenState: StateFlow<String> = mutableTokenState

    private val log = log.withTag("AuthViewModel")


    fun login(username :String, password:String) = viewModelScope.launch {
        _loginUIState.value = LoginUIState.Loading

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
            mutableTokenState.value = loginResponse.access_token
            _loginUIState.value = LoginUIState.Success
        }
        else{
            _loginUIState.value = LoginUIState.Error(loginResponse.message)
        }

    }

    private fun handleMainError(throwable: Throwable) {
        log.e(throwable) { "Error" }
    }
}
