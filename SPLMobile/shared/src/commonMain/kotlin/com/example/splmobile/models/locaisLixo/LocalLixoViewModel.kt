package com.example.splmobile.models.locaisLixo

import co.touchlab.kermit.Logger
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.models.ViewModel
import com.example.splmobile.services.locaisLixo.LocalLixoService
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LocalLixoViewModel (
    private val localLixoRepository: LocalLixoRepository,
    private val localLixoService: LocalLixoService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("LocalLixoViewModel")


    //state get all locais lixo
    private val _locaisLixoUIState = MutableStateFlow<LocaisLixoUIState>(LocaisLixoUIState.Empty)
    val locaisLixoUIState = _locaisLixoUIState.asStateFlow()
    sealed class LocaisLixoUIState {
        data class Success(val locaisLixo: List<LocalLixoSer>) : LocaisLixoUIState()
        data class Error(val error: String) : LocaisLixoUIState()
        object Loading : LocaisLixoUIState()
        object Offline : LocaisLixoUIState()
        object Empty : LocaisLixoUIState()
    }

    //state create local lixo
    private val _localLixoCreateUIState = MutableStateFlow<LocalLixoCreateUIState>(LocalLixoCreateUIState.Empty)
    val localLixoCreateUIState = _localLixoCreateUIState.asStateFlow()
    sealed class LocalLixoCreateUIState {
        object Success: LocalLixoCreateUIState()
        data class Error(val error: String) : LocalLixoCreateUIState()
        object Loading : LocalLixoCreateUIState()
        object Offline : LocalLixoCreateUIState()
        object Empty : LocalLixoCreateUIState()
    }

    //state update local lixo
    private val _localLixoUpdateUIState = MutableStateFlow<LocalLixoUpdateUIState>(LocalLixoUpdateUIState.Empty)
    val localLixoUpdateUIState = _localLixoCreateUIState.asStateFlow()
    sealed class LocalLixoUpdateUIState {
        object Success: LocalLixoUpdateUIState()
        data class Error(val error: String) : LocalLixoUpdateUIState()
        object Loading : LocalLixoUpdateUIState()
        object Offline : LocalLixoUpdateUIState()
        object Empty : LocalLixoUpdateUIState()
    }


    init {
        getLocaisLixo()
    }

    override fun onCleared() {
        log.v("Clearing LocalLixoViewModel")
    }

    fun getLocaisLixo() {
        _locaisLixoUIState.value = LocaisLixoUIState.Loading
        viewModelScope.launch {
            val response = localLixoService.getLocaisLixo()

            if(response.status == "200"){
                _locaisLixoUIState.value = LocaisLixoUIState.Success(response.data)
            }else{
                _locaisLixoUIState.value = LocaisLixoUIState.Error(response.message)
            }
        }

    }

     fun createLocalLixo(localLixo: LocalLixoSer) {
         _localLixoCreateUIState.value = LocalLixoCreateUIState.Loading
         viewModelScope.launch {
             val response = localLixoService.postLocalLixo(localLixo)

             if(response.status == "200"){
                 _localLixoCreateUIState.value = LocalLixoCreateUIState.Success
             }else{
                 _localLixoCreateUIState.value = LocalLixoCreateUIState.Error(response.message)
             }
         }
    }

    fun updateLocalLixoEstado(localLixo: LocalLixoSer, estado: String, token: String){
        _localLixoCreateUIState.value = LocalLixoCreateUIState.Loading
        viewModelScope.launch {
            val response = localLixoService.patchLocalLixoEstado(localLixo,estado,token)

            if(response.status == "200"){
                _localLixoCreateUIState.value = LocalLixoCreateUIState.Success
            }else{
                _localLixoCreateUIState.value = LocalLixoCreateUIState.Error(response.message)
            }
        }
    }
    private fun handleLocalLixoError(throwable: Throwable) {
        log.e(throwable) { "Error downloading LocalLixo list" }

    }


}


