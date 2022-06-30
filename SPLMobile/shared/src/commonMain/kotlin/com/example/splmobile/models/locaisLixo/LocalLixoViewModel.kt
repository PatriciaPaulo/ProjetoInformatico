package com.example.splmobile.models.locaisLixo

import co.touchlab.kermit.Logger
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.models.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LocalLixoViewModel (
    private val localLixoRepository: LocalLixoRepository,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("LocalLixoViewModel")


    private val mutableLocaisLixoState: MutableStateFlow<LocalLixoViewState> =
        MutableStateFlow(LocalLixoViewState(isLoading = true))

    val locaisLixoState= mutableLocaisLixoState.asStateFlow()


    init {
        observeLocaisLixo()
    }

    override fun onCleared() {
        log.v("Clearing LocalLixoViewModel")
    }
    private fun observeLocaisLixo() {
        // Refresh LocaisLixo, and emit any exception that was thrown so we can handle it downstream
        val refreshFlow = flow<Throwable?> {
            try {
                localLixoRepository.refreshLocaisLixo()
                emit(null)
            } catch (exception: Exception) {
                emit(exception)
            }
        }

        viewModelScope.launch {
            //collect locaislixo from repository and if stale from api
            combine(refreshFlow, localLixoRepository.getLocaisLixo()) { throwable, locaisLixo -> throwable to locaisLixo }
                .collect { (error, locaisLixo) ->
                    mutableLocaisLixoState.update { previousState ->
                        val errorMessage = if (error != null) {
                            "Unable to download localLixo list"
                        } else {
                            previousState.error
                        }
                        //log.v { "in observe " + locaisLixo.distinct().toList().toString()}
                        LocalLixoViewState(
                            isLoading = false,
                            locaisLixo =  locaisLixo.distinct().toList().takeIf { it.isNotEmpty() },
                            error = errorMessage.takeIf { locaisLixo.isEmpty() },
                            isEmpty = locaisLixo.isEmpty() && errorMessage == null
                        )
                    }
                }
        }
    }


    suspend fun getLocalLixoInfo(id: Long): LocalLixo?  {
        var localLixo: LocalLixo? = null
         viewModelScope.async {
            log.v { "GetLocalLixo" }
            try {
                localLixo = localLixoRepository.getLocalLixoById(id)
            } catch (exception: Exception) {
                handleLocalLixoError(exception)
            }
        }.await()
        return localLixo
    }

    fun refreshLocaisLixo(): Job {
        // Set loading state, which will be cleared when the repository re-emits
        mutableLocaisLixoState.update { it.copy(isLoading = true) }
        return viewModelScope.launch {
            log.v { "refreshLocaisLixo" }
            try {
                localLixoRepository.refreshLocaisLixo()
            } catch (exception: Exception) {
                handleLocalLixoError(exception)
            }
        }
    }

    fun deleteLocaisLixo(): Job {
        // Set loading state, which will be cleared when the repository re-emits
        mutableLocaisLixoState.update { it.copy(isLoading = true) }
        return viewModelScope.launch {
            log.v { "deleteLocaisLixo" }
            try {
                localLixoRepository.deleteLocaisLixo()
            } catch (exception: Exception) {
                handleLocalLixoError(exception)
            }
        }
    }

    suspend fun createLocalLixo(localLixo: LocalLixo, token: String): String {
        var response: String = ""
        viewModelScope.async {
            log.v { "CreateLocalLixo" }
            try {
                response = localLixoRepository.createLocalLixo(localLixo,token)
            } catch (exception: Exception) {
                handleLocalLixoError(exception)
            }
        }.await()
        log.v { "message: ${ response }" }
        return response
    }

    private fun handleLocalLixoError(throwable: Throwable) {
        log.e(throwable) { "Error downloading LocalLixo list" }
        mutableLocaisLixoState.update {
            if (it.locaisLixo.isNullOrEmpty()) {
                LocalLixoViewState(error = "Unable to refresh localLixo list")
            } else {
                // Just let it fail silently if we have a cache
                it.copy(isLoading = false)
            }
        }
    }

    suspend fun updateLocalLixoEstado(localLixo: LocalLixo, s: String) : String{
        var response: String = ""
        viewModelScope.async {
            log.v { "CreateLocalLixo" }
            try {
                response = localLixoRepository.updateLocalLixoEstado(localLixo,s)
            } catch (exception: Exception) {
                handleLocalLixoError(exception)
            }
        }.await()
        log.v { "message: ${ response }" }
        return response
    }
}

data class LocalLixoViewState(
    val locaisLixo: List<LocalLixo>? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
