package com.example.splmobile.models.UtilizadorInfo

import UtilizadorInfoService
import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UtilizadorResponse
import com.example.splmobile.dtos.myInfo.UtilizadorSer
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.ViewModel
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import com.example.splmobile.services.locaisLixo.LocalLixoService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UtilizadorInfoViewModel (
    private val utilizadorInfoService: UtilizadorInfoService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("UtilizadorInfoViewModel")

    //state get me utilizador
    private val _myInfoUtilizadorUIState = MutableStateFlow<MyInfoUtilizadorUIState>(MyInfoUtilizadorUIState.Empty)
    val myInfoUtilizadorUIState = _myInfoUtilizadorUIState.asStateFlow()
    sealed class MyInfoUtilizadorUIState {
        data class Success(val data: UtilizadorSer) : MyInfoUtilizadorUIState()
        data class Error(val error: String) : MyInfoUtilizadorUIState()
        object Loading : MyInfoUtilizadorUIState()
        object Offline : MyInfoUtilizadorUIState()
        object Empty : MyInfoUtilizadorUIState()
    }

    //state put me utilizador
    private val _myInfoUtilizadorAtualizarUIState = MutableStateFlow<MyInfoUtilizadorAtualizarUIState>(MyInfoUtilizadorAtualizarUIState.Empty)
    val myInfoUtilizadorAtualizarUIState = _myInfoUtilizadorAtualizarUIState.asStateFlow()
    sealed class MyInfoUtilizadorAtualizarUIState {
        data class Success(val data: UtilizadorSer) : MyInfoUtilizadorAtualizarUIState()
        data class Error(val error: String) : MyInfoUtilizadorAtualizarUIState()
        object Loading : MyInfoUtilizadorAtualizarUIState()
        object Offline : MyInfoUtilizadorAtualizarUIState()
        object Empty : MyInfoUtilizadorAtualizarUIState()
    }

    //state get my locais lixo
    private val _myLocaisLixoUIState = MutableStateFlow<MyLocaisLixoUIState>(MyLocaisLixoUIState.Empty)
    val mylocaisLixoUIState = _myLocaisLixoUIState.asStateFlow()
    sealed class MyLocaisLixoUIState {
        data class Success(val locaisLixo: List<LocalLixoSer>) : MyLocaisLixoUIState()
        data class Error(val error: String) : MyLocaisLixoUIState()
        object Loading : MyLocaisLixoUIState()
        object Offline : MyLocaisLixoUIState()
        object Empty : MyLocaisLixoUIState()
    }

    fun getMyInfo(token: String) {
        _myInfoUtilizadorUIState.value = MyInfoUtilizadorUIState.Loading
        log.v("Getting All User Info ")
        viewModelScope.launch {
            val response = utilizadorInfoService.getUtilizador(token)

            if(response.status == "200"){
                _myInfoUtilizadorUIState.value = MyInfoUtilizadorUIState.Success(response.data)
            }else{
                if(response.status == "500"){
                    _myInfoUtilizadorUIState.value =MyInfoUtilizadorUIState.Offline
                }
                else{
                    _myInfoUtilizadorUIState.value =MyInfoUtilizadorUIState.Error(response.status)
                }
            }

        }

    }
    fun putMyInfo(utilizador :UtilizadorSer,token: String) {
        _myInfoUtilizadorAtualizarUIState.value = MyInfoUtilizadorAtualizarUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = utilizadorInfoService.putUtilizador(utilizador,token)

            if(response.status == "200"){
                _myInfoUtilizadorAtualizarUIState.value = MyInfoUtilizadorAtualizarUIState.Success(response.data)
            }else{
                if(response.status == "500"){
                    _myInfoUtilizadorAtualizarUIState.value =MyInfoUtilizadorAtualizarUIState.Offline
                }
                else{
                    _myInfoUtilizadorAtualizarUIState.value =MyInfoUtilizadorAtualizarUIState.Error(response.status)
                }
            }

        }

    }

    fun getMyLocaisLixo(token: String) {
        _myLocaisLixoUIState.value = MyLocaisLixoUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = utilizadorInfoService.getMyLocaisLixo(token)

            if(response.status == "200"){
                _myLocaisLixoUIState.value = MyLocaisLixoUIState.Success(response.data)
            }else{
                _myLocaisLixoUIState.value =MyLocaisLixoUIState.Error(response.status)
            }
        }

    }

    // Check Email
    fun checkEmail(email: String) = viewModelScope.launch {
        _myInfoUtilizadorUIState.value = UtilizadorInfoViewModel.MyInfoUtilizadorUIState.Loading

        var emailResponse : EmailCheckResponse = viewModelScope.async() {
            log.v { "getEmailExists" }
            try {
                utilizadorInfoService.checkMyEmail(EmailRequest(email))
            } catch (exception : Exception) {
                handleMainError(exception)
            }
        }.await() as EmailResponse
    }

    fun login(email : String, password : String) = viewModelScope.launch {
        // Set loading state, which will be cleared when the repository re-emits
        _loginUIState.value = AuthViewModel.LoginUIState.Loading

        var loginResponse : LoginResponse = viewModelScope.async(){
            log.v { "postLogin" }
            try {
                authService.postLogin(LoginRequest(email,password))
            } catch (exception: Exception) {
                handleMainError(exception)
            }
        }.await() as LoginResponse

        if(loginResponse.status == "200"){
            mutableTokenState.value = loginResponse.access_token
            _loginUIState.value = AuthViewModel.LoginUIState.Success
        }
        else{
            _loginUIState.value = AuthViewModel.LoginUIState.Error(loginResponse.message)
        }
    }
}
