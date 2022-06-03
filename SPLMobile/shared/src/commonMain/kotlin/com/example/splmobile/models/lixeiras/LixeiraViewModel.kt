package com.example.splmobile.models.lixeiras

import co.touchlab.kermit.Logger
import com.example.splmobile.database.Lixeira
import com.example.splmobile.models.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LixeiraViewModel (
    private val lixeiraRepository: LixeiraRepository,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("LixeiraViewModel")

    private val mutableLixeiraState: MutableStateFlow<LixeiraViewState> =
        MutableStateFlow(LixeiraViewState(isLoading = true))

    val lixeiraState: StateFlow<LixeiraViewState> = mutableLixeiraState

    init {
        observeLixeiras()
    }

    override fun onCleared() {
        log.v("Clearing LixeiraViewModel")
    }

    private fun observeLixeiras() {
        // Refresh Lixeiras, and emit any exception that was thrown so we can handle it downstream
        val refreshFlow = flow<Throwable?> {
            try {
                lixeiraRepository.refreshLixeirasIfStale()
                emit(null)
            } catch (exception: Exception) {
                emit(exception)
            }
        }

        viewModelScope.launch {
           combine(refreshFlow, lixeiraRepository.getLixeiras()) { throwable, lixeiras -> throwable to lixeiras }
                .collect { (error, lixeiras) ->

                    mutableLixeiraState.update { previousState ->
                        val errorMessage = if (error != null) {
                            "Unable to download Lixeira list"
                        } else {
                            previousState.error
                        }
                        LixeiraViewState(
                            isLoading = false,
                            lixeiras =  lixeiras.distinct().toList().takeIf { it.isNotEmpty() },
                            error = errorMessage.takeIf { lixeiras.isEmpty() },
                            isEmpty = lixeiras.isEmpty() && errorMessage == null
                        )
                    }
                }
        }
    }

    fun refreshLixeiras(): Job {
        // Set loading state, which will be cleared when the repository re-emits
        mutableLixeiraState.update { it.copy(isLoading = true) }
        return viewModelScope.launch {
            log.v { "refreshLixeiras" }
            try {
                lixeiraRepository.refreshLixeiras()
            } catch (exception: Exception) {
                handleLixeiraError(exception)
            }
        }
    }

    fun deleteLixeiras(): Job {
        // Set loading state, which will be cleared when the repository re-emits
        mutableLixeiraState.update { it.copy(isLoading = true) }
        return viewModelScope.launch {
            log.v { "deleteLixeiras" }
            try {
                lixeiraRepository.deleteLixeiras()
            } catch (exception: Exception) {
                handleLixeiraError(exception)
            }
        }
    }


    private fun handleLixeiraError(throwable: Throwable) {
        log.e(throwable) { "Error downloading Lixeira list" }
        mutableLixeiraState.update {
            if (it.lixeiras.isNullOrEmpty()) {
                LixeiraViewState(error = "Unable to refresh Lixeira list")
            } else {
                // Just let it fail silently if we have a cache
                it.copy(isLoading = false)
            }
        }
    }
}

data class LixeiraViewState(
    val lixeiras: List<Lixeira>? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
