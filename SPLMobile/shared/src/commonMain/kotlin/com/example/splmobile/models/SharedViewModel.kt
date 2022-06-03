package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.ktor.other.requestsAPI
import com.example.splmobile.models.lixeiras.LixeiraViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

//requests not related to FLASK API
class SharedViewModel(
    private val requestsAPI: requestsAPI,
    log: Logger

): ViewModel() {

    private val log = log.withTag("sharedViewModel")



   suspend fun getCoordenadas(nome :String): String {
        // Set loading state, which will be cleared when the repository re-emits

        var coor =  viewModelScope.async(){
            log.v { "getCoordenadas" }
            try {
               requestsAPI.postJsonFromApi(nome)
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
