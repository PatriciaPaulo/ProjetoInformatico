package com.example.splmobile.viewmodels

import co.touchlab.kermit.Logger
import com.example.splmobile.objects.activities.ActivityTypeSerializable
import com.example.splmobile.objects.activities.CreateActivitySerializable
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

    sealed class ActivityTypeUIState {
        data class Success(val activityTypes: List<ActivityTypeSerializable>) : ActivityTypeUIState()
        data class Error(val error: String) : ActivityTypeUIState()
        object Loading : ActivityTypeUIState()
        object Empty : ActivityTypeUIState()
    }

    // Variables
    private val _activityCreateUIState = MutableStateFlow<ActivityStartUIState>(ActivityStartUIState.Empty)
    val activityCreateUIState = _activityCreateUIState.asStateFlow()

    private val _activityTypeUIState = MutableStateFlow<ActivityTypeUIState>(ActivityTypeUIState.Empty)
    val activityTypeUIState = _activityTypeUIState.asStateFlow()

    // Requests

    // Create New Activity
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

    // Get All Activity Types
    fun getActivityTypes() {
        _activityTypeUIState.value = ActivityTypeUIState.Loading

        viewModelScope.launch {
            val response = activityService.getActivityTypes()
            if(response.message.substring(0,3) == "200") {
                _activityTypeUIState.value = ActivityTypeUIState.Success(response.data)
            } else {
                _activityTypeUIState.value = ActivityTypeUIState.Error(response.message)
            }
        }
    }
}