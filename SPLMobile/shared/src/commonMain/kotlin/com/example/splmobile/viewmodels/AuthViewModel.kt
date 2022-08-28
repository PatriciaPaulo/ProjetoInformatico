package com.example.splmobile.viewmodels

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.dtos.auth.SignInRequest
import com.example.splmobile.dtos.auth.SignInResponse
import com.example.splmobile.services.auth.AuthService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel (
    private val authService: AuthService,
    log: Logger
) : ViewModel() {

    // Sealed Classes
    sealed class LoginUIState{
        object Success : LoginUIState()
        data class Error(val message: String) : LoginUIState()
        object Loading : LoginUIState()
        object Empty : LoginUIState() //Empty used to create
    }

    sealed class RegisterUIState{
        object Success : RegisterUIState()
        data class Error(val message: String) : RegisterUIState()
        object Loading : RegisterUIState()
        object Empty : RegisterUIState()
    }


    // Variables Declaration
    private val _loginUIState = MutableStateFlow<LoginUIState>(LoginUIState.Empty)
    private val mutableTokenState: MutableStateFlow<String> = MutableStateFlow("")
    private val _registerUIState = MutableStateFlow<RegisterUIState>(RegisterUIState.Empty)
    val loginUIState: StateFlow<LoginUIState> = _loginUIState
    val registerUIState: StateFlow<RegisterUIState> = _registerUIState
    val tokenState : StateFlow<String> = mutableTokenState

    private val log = log.withTag("AuthViewModel")

    // API Requests
    // Login
    fun login(email : String, password : String) = viewModelScope.launch {
        // Set loading state, which will be cleared when the repository re-emits
        _loginUIState.value = LoginUIState.Loading
        try {
            var loginResponse : LoginResponse = viewModelScope.async(){
                log.v { "postLogin" }

                    authService.postLogin(LoginRequest(email,password))

            }.await() as LoginResponse

            if(loginResponse.message.substring(0,3)  == "200"){
                mutableTokenState.value = loginResponse.access_token
                _loginUIState.value = LoginUIState.Success
            }
            else{
                _loginUIState.value = LoginUIState.Error(loginResponse.message)
            }

        } catch (exception: Exception) {
            _loginUIState.value = LoginUIState.Error(exception.message.toString())
        }
    }

    // Register New User
    fun registerUser(email: String, password: String, passwordConfirmation: String) = viewModelScope.launch {
        _registerUIState.value = RegisterUIState.Loading

        var registerResponse : SignInResponse = viewModelScope.async {
            try {
                authService.postSignIn(SignInRequest(email, password, passwordConfirmation))
            } catch (e : Exception) {
                handleMainError(e)
            }
        }.await() as SignInResponse

        if(registerResponse.status == 200){
            _registerUIState.value = RegisterUIState.Success
        } else {
            _registerUIState.value = RegisterUIState.Error(registerResponse.message)
        }
    }

    private fun handleMainError(throwable: Throwable) {
        log.e(throwable) { "Error" }
    }

    fun setGuest(){
        mutableTokenState.value = "0"
    }
}
