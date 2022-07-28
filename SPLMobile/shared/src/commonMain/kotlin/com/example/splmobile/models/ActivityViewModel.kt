package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.activities.CreateActivitySerializable
import com.example.splmobile.services.activities.ActivityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel (
    private val activityService: ActivityService,
    log: Logger
) : ViewModel() {

    // Classes
    sealed class ActivityStartUIState {
        object Success: ActivityStartUIState()
        data class Error(val error : String) : ActivityStartUIState()
        object Loading : ActivityStartUIState()
        object Empty : ActivityStartUIState()
    }

    // Variables
    private val _activityCreateUIState = MutableStateFlow<ActivityStartUIState>(ActivityStartUIState.Empty)
    val activityCreateUIState = _activityCreateUIState.asStateFlow()

    fun createActivity(activity : CreateActivitySerializable, token : String) {
        _activityCreateUIState.value = ActivityStartUIState.Loading

        viewModelScope.launch {
            val response = activityService.postCreateActivity(activity, token)
            if(response.message.substring(0,3) == "200"){
                _activityCreateUIState.value = ActivityStartUIState.Success
            } else {
                _activityCreateUIState.value = ActivityStartUIState.Error(response.message)
            }
        }
    }
}