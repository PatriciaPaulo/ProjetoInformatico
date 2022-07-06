package com.example.splmobile.models.UtilizadorInfo

import UtilizadorInfoService
import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.dtos.myInfo.UtilizadorResponse
import com.example.splmobile.dtos.myInfo.UtilizadorSer
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.ViewModel
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import com.example.splmobile.services.locaisLixo.LocalLixoService
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

    fun getMyInfo(authViewModel: AuthViewModel) {
        _myInfoUtilizadorUIState.value = MyInfoUtilizadorUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = utilizadorInfoService.getUtilizador(authViewModel.tokenState.value)

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

    fun getMyLocaisLixo(authViewModel: AuthViewModel) {
        _myLocaisLixoUIState.value = MyLocaisLixoUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = utilizadorInfoService.getMyLocaisLixo(authViewModel.tokenState.value)

            if(response.status == "200"){
                _myLocaisLixoUIState.value = MyLocaisLixoUIState.Success(response.data)
            }else{
                _myLocaisLixoUIState.value =MyLocaisLixoUIState.Error(response.status)
            }
        }

    }
}
