package com.example.splmobile.models

import co.touchlab.kermit.Logger

import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.services.other.requestsAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

//requests not related to FLASK API
class SharedViewModel(
    private val requestsAPI: requestsAPI,
    log: Logger

): ViewModel() {

    private val log = log.withTag("sharedViewModel")
    sealed class CoordenatesUIState{
        data class Success(val latitude: Double,val longitude: Double) : CoordenatesUIState()
        data class Error(val message: String) : CoordenatesUIState()
        object Loading : CoordenatesUIState()
        object Empty : CoordenatesUIState() //Empty used to create
    }

    private val _coordenatesUIState = MutableStateFlow<CoordenatesUIState>(CoordenatesUIState.Empty)
    val coordenatesUIState: StateFlow<CoordenatesUIState> = _coordenatesUIState

   fun getCoordenadas(nome :String) = viewModelScope.launch {
       // Set loading state, which will be cleared when the repository re-emits
       _coordenatesUIState.value = CoordenatesUIState.Loading

       var coordenadas: String = viewModelScope.async {
           log.v { "get coordenadas" }
           try {
               requestsAPI.getJsonFromApi(nome)
           } catch (exception: Exception) {
               log.e(exception) { "Error" }
           }
       }.await() as String

       if( !Json.parseToJsonElement(coordenadas).jsonObject.containsKey("error") ){
           _coordenatesUIState.value = CoordenatesUIState.Success(
           Json.parseToJsonElement(coordenadas).jsonObject.get("latt").toString().removePrefix("\"").removeSuffix("\"").toDouble(),
           Json.parseToJsonElement(coordenadas).jsonObject.get("longt").toString().removePrefix("\"").removeSuffix("\"").toDouble())

       }else{
           _coordenatesUIState.value = CoordenatesUIState.Error(Json.parseToJsonElement(coordenadas).jsonObject.get("error").toString())

       }


    }

}
