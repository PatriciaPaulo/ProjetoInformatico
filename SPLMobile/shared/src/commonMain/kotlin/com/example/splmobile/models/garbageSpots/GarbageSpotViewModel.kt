package com.example.splmobile.models.garbageSpots

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.models.ViewModel

import com.example.splmobile.services.garbageSpots.GarbageSpotService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GarbageSpotViewModel (
    private val garbageSpotRepository: LocalLixoRepository,
    private val garbageSpotService: GarbageSpotService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("GarbageSpotViewModel")


    //state get all locais lixo
    private val _garbageSpotsUIState = MutableStateFlow<GarbageSpotsUIState>(GarbageSpotsUIState.Empty)
    val garbageSpotsUIState = _garbageSpotsUIState.asStateFlow()
    sealed class GarbageSpotsUIState {
        data class Success(val garbageSpots: List<GarbageSpotSerializable>) : GarbageSpotsUIState()
        data class Error(val error: String) : GarbageSpotsUIState()
        object Loading : GarbageSpotsUIState()
        object Offline : GarbageSpotsUIState()
        object Empty : GarbageSpotsUIState()
    }

    //state create garbage spot
    private val _garbageSpotCreateUIState = MutableStateFlow<GarbageSpotCreateUIState>(GarbageSpotCreateUIState.Empty)
    val garbageSpotCreateUIState = _garbageSpotCreateUIState.asStateFlow()
    sealed class GarbageSpotCreateUIState {
        object Success: GarbageSpotCreateUIState()
        data class Error(val error: String) : GarbageSpotCreateUIState()
        object Loading : GarbageSpotCreateUIState()
        object Offline : GarbageSpotCreateUIState()
        object Empty : GarbageSpotCreateUIState()
    }

    //state update garbage spot
    private val _garbageSpotUpdateUIState = MutableStateFlow<GarbageSpotUpdateUIState>(GarbageSpotUpdateUIState.Empty)
    val garbageSpotUpdateUIState = _garbageSpotCreateUIState.asStateFlow()
    sealed class GarbageSpotUpdateUIState {
        object Success: GarbageSpotUpdateUIState()
        data class Error(val error: String) : GarbageSpotUpdateUIState()
        object Loading : GarbageSpotUpdateUIState()
        object Offline : GarbageSpotUpdateUIState()
        object Empty : GarbageSpotUpdateUIState()
    }


    init {
        getGarbageSpots()
    }

    override fun onCleared() {
        log.v("Clearing GarbageSpotViewModel")
    }

    fun getGarbageSpots() {
        _garbageSpotsUIState.value = GarbageSpotsUIState.Loading
        log.v("getting all garbage spot ")
        viewModelScope.launch {
            val response = garbageSpotService.getGarbageSpots()

            if(response.status == "200"){
                _garbageSpotsUIState.value = GarbageSpotsUIState.Success(response.data)
            }else{
                _garbageSpotsUIState.value = GarbageSpotsUIState.Error(response.status)
            }
        }

    }

     fun createGarbageSpot(garbageSpot: GarbageSpotSerializable, token: String) {
         log.v("creating garbage spot $garbageSpot")
         _garbageSpotCreateUIState.value = GarbageSpotCreateUIState.Loading
         viewModelScope.launch {
             val response = garbageSpotService.postGarbageSpot(garbageSpot,token)
             if(response.status == "200"){
                 log.v("Creating garbage spot successful")
                 _garbageSpotCreateUIState.value = GarbageSpotCreateUIState.Success
                 getGarbageSpots()
             }else{
                 log.v("Creating garbage spot error")
                 _garbageSpotCreateUIState.value = GarbageSpotCreateUIState.Error(response.message)
             }
         }

    }

    fun updateGarbageSpotEstado(garbageSpot: GarbageSpotSerializable, estado: String, token: String){
        log.v("updating status garbage spot $garbageSpot")
        _garbageSpotUpdateUIState.value = GarbageSpotUpdateUIState.Loading
        viewModelScope.launch {
            val response = garbageSpotService.patchGarbageSpotStatus(garbageSpot,estado,token)

            if(response.status == "200"){
                log.v("updating garbage spot successful")
                _garbageSpotUpdateUIState.value = GarbageSpotUpdateUIState.Success
                getGarbageSpots()
            }else{
                log.v("updating garbage spot error")
                _garbageSpotUpdateUIState.value = GarbageSpotUpdateUIState.Error(response.message)
            }
        }

    }


    private fun handleGarbageSpotError(throwable: Throwable) {
        log.e(throwable) { "Error downloading GarbageSpot list" }

    }


}


