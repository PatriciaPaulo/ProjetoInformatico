package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.services.other.requestsAPI
import kotlinx.coroutines.async

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
               requestsAPI.getJsonFromApi(nome)
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
