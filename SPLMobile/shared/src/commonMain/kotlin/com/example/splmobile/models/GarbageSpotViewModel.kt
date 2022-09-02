package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.dtos.garbageTypes.GarbageTypeDTO
import com.example.splmobile.dtos.garbageTypes.UnitTypeDTO

import com.example.splmobile.services.garbageSpots.GarbageSpotService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GarbageSpotViewModel (
    private val garbageSpotService: GarbageSpotService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("GarbageSpotViewModel")


    //state get all locais lixo
    private val _garbageSpotsUIState = MutableStateFlow<GarbageSpotsUIState>(GarbageSpotsUIState.Empty)
    val garbageSpotsUIState = _garbageSpotsUIState.asStateFlow()
    sealed class GarbageSpotsUIState {
        data class Success(val garbageSpots: List<GarbageSpotDTO>) : GarbageSpotsUIState()
        data class GarbageSpotByIdSuccess(val garbageSpot: GarbageSpotDTO) : GarbageSpotsUIState()
        data class Error(val error: String) : GarbageSpotsUIState()
        object Loading : GarbageSpotsUIState()
        object Empty : GarbageSpotsUIState()
    }

    //state create garbage spot
    private val _garbageSpotCreateUIState = MutableStateFlow<GarbageSpotCreateUIState>(
        GarbageSpotCreateUIState.Empty
    )
    val garbageSpotCreateUIState = _garbageSpotCreateUIState.asStateFlow()
    sealed class GarbageSpotCreateUIState {
        object Success: GarbageSpotCreateUIState()
        data class Error(val error: String) : GarbageSpotCreateUIState()
        object Loading : GarbageSpotCreateUIState()
        object Empty : GarbageSpotCreateUIState()
    }

    //state update garbage spot
    private val _garbageSpotUpdateUIState = MutableStateFlow<GarbageSpotUpdateUIState>(
        GarbageSpotUpdateUIState.Empty
    )
    val garbageSpotUpdateUIState = _garbageSpotUpdateUIState.asStateFlow()
    sealed class GarbageSpotUpdateUIState {
        object Success: GarbageSpotUpdateUIState()
        data class Error(val error: String) : GarbageSpotUpdateUIState()
        object Loading : GarbageSpotUpdateUIState()

        object Empty : GarbageSpotUpdateUIState()
    }

    //state get garbage types
    private val _garbageTypeUIState = MutableStateFlow<GarbageTypesUIState>(GarbageTypesUIState.Empty)
    val garbageTypesUIState = _garbageTypeUIState.asStateFlow()
    sealed class GarbageTypesUIState {
        data class Success(val garbageTypes: List<GarbageTypeDTO>) : GarbageTypesUIState()
        data class Error(val error: String) : GarbageTypesUIState()
        object Loading : GarbageTypesUIState()
        object Empty : GarbageTypesUIState()
    }

    //State Get UnitTypes
    private val _unitTypeUIState = MutableStateFlow<UnitTypesUIState>(UnitTypesUIState.Empty)
    val unitTypeUIState = _unitTypeUIState.asStateFlow()
    sealed class UnitTypesUIState {
        data class Success(val unitTypes: List<UnitTypeDTO>) : UnitTypesUIState()
        data class Error(val error: String) : UnitTypesUIState()
        object Loading : UnitTypesUIState()
        object Empty : UnitTypesUIState()
    }


    override fun onCleared() {
        log.v("Clearing GarbageSpotViewModel")
    }

    fun getGarbageSpots(token: String) {
        _garbageSpotsUIState.value = GarbageSpotsUIState.Loading
        log.v("getting all garbage spot ")
        viewModelScope.launch {
            val response = garbageSpotService.getGarbageSpots(token)

            if(response.message.substring(0,3)  == "200"){
                _garbageSpotsUIState.value = GarbageSpotsUIState.Success(response.data)
            }else{
                _garbageSpotsUIState.value = GarbageSpotsUIState.Error(response.message)
            }
        }

    }

    fun getGarbageSpotById(gsId: Long,token: String) {
        _garbageSpotsUIState.value = GarbageSpotsUIState.Loading
        log.v("getting garbage spots by id ")
        viewModelScope.launch {
            val response = garbageSpotService.getGarbageSpotById(gsId,token)

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _garbageSpotsUIState.value =
                    GarbageSpotsUIState.GarbageSpotByIdSuccess(response.data)
            }else{
                _garbageSpotsUIState.value = GarbageSpotsUIState.Error(response.message)
            }
        }
    }
     fun createGarbageSpot(garbageSpot: GarbageSpotDTO, token: String) {
         log.v("creating garbage spot $garbageSpot")
         _garbageSpotCreateUIState.value = GarbageSpotCreateUIState.Loading
         viewModelScope.launch {
             val response = garbageSpotService.postGarbageSpot(garbageSpot,token)
             if(response.message.substring(0,3)  == "200"){
                 log.v("Creating garbage spot successful")
                 _garbageSpotCreateUIState.value = GarbageSpotCreateUIState.Success

             }else{
                 log.v("Creating garbage spot error")
                 _garbageSpotCreateUIState.value = GarbageSpotCreateUIState.Error(response.message)
             }
         }

    }

    fun updateGarbageSpotEstado(garbageSpot: GarbageSpotDTO, estado: String, token: String){
        log.v("updating status garbage spot $garbageSpot")
        _garbageSpotUpdateUIState.value = GarbageSpotUpdateUIState.Loading
        viewModelScope.launch {
            val response = garbageSpotService.patchGarbageSpotStatus(garbageSpot,estado,token)

            if(response.message.substring(0,3)  == "200"){
                log.v("updating garbage spot successful")
                _garbageSpotUpdateUIState.value = GarbageSpotUpdateUIState.Success

            }else{
                log.v("updating garbage spot error")
                _garbageSpotUpdateUIState.value = GarbageSpotUpdateUIState.Error(response.message)
            }
        }

    }

    //garbage type section
    fun getGarbageTypes(token: String) {
        _garbageTypeUIState.value = GarbageTypesUIState.Loading
        log.v("getting all garbage types")
        viewModelScope.launch {
            val response = garbageSpotService.getGarbageTypes(token)

            if(response.message.substring(0,3)  == "200"){
                _garbageTypeUIState.value = GarbageTypesUIState.Success(response.data)
            }else{
                _garbageTypeUIState.value = GarbageTypesUIState.Error(response.message)
            }
        }
    }

    // Get Unit Types
    fun getUnitTypes() {
        _unitTypeUIState.value = UnitTypesUIState.Loading
        log.v("Getting Unit Types")
        viewModelScope.launch {
            val response = garbageSpotService.getUnitTypes()

            if(response.message.substring(0,3)  == "200"){
                _unitTypeUIState.value = UnitTypesUIState.Success(response.data)
            }else{
                _unitTypeUIState.value = UnitTypesUIState.Error(response.message)
            }
        }
    }



}


