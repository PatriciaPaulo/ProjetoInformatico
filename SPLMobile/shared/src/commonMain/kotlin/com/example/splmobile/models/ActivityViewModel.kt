package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.objects.activities.*
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
        data class Success(val currentActivity : Int): ActivityStartUIState()
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

    sealed class GarbageInActivityUIState {
        data class Success(val activities: List<ExplicitGarbageInActivityDTO>) : GarbageInActivityUIState()
        data class Error(val error: String) : GarbageInActivityUIState()
        object Loading : GarbageInActivityUIState()
        object Empty : GarbageInActivityUIState()
    }

    sealed class UpdateGarbageInActivityUIState {
        object Success : UpdateGarbageInActivityUIState()
        data class Error(val error: String) : UpdateGarbageInActivityUIState()
        object Loading : UpdateGarbageInActivityUIState()
        object Empty : UpdateGarbageInActivityUIState()
    }

    sealed class AddGarbageInActivityUIState {
        data class Success(val newGarbage: GarbageInActivityDTO) : AddGarbageInActivityUIState()
        data class Error(val error: String) : AddGarbageInActivityUIState()
        object Loading : AddGarbageInActivityUIState()
        object Empty : AddGarbageInActivityUIState()
    }

    sealed class DeleteGarbageInActivityUIState {
        object Success: DeleteGarbageInActivityUIState()
        data class Error(val error: String) : DeleteGarbageInActivityUIState()
        object Loading : DeleteGarbageInActivityUIState()
        object Empty : DeleteGarbageInActivityUIState()
    }


    // Variables
    private var currentActivity : Int = 0

    private val _activityCreateUIState = MutableStateFlow<ActivityStartUIState>(ActivityStartUIState.Empty)
    val activityCreateUIState = _activityCreateUIState.asStateFlow()

    private val _activityTypeUIState = MutableStateFlow<ActivityTypeUIState>(ActivityTypeUIState.Empty)
    val activityTypeUIState = _activityTypeUIState.asStateFlow()

    private val _garbageInActivityUIState = MutableStateFlow<GarbageInActivityUIState>(GarbageInActivityUIState.Empty)
    val garbageInActivityUIState = _garbageInActivityUIState.asStateFlow()

    private val _updateGarbageInActivityUIState = MutableStateFlow<UpdateGarbageInActivityUIState>(UpdateGarbageInActivityUIState.Empty)
    val updateGarbageInActivityUIState = _updateGarbageInActivityUIState.asStateFlow()

    private val _addGarbageInActivityUIState = MutableStateFlow<AddGarbageInActivityUIState>(AddGarbageInActivityUIState.Empty)
    val addGarbageInActivityUIState = _addGarbageInActivityUIState.asStateFlow()

    private val _deleteGarbageInActivity = MutableStateFlow<DeleteGarbageInActivityUIState>(DeleteGarbageInActivityUIState.Empty)
    val deleteGarbageInActivity = _deleteGarbageInActivity.asStateFlow()

    fun getCurrentActivity() : Int {
        return currentActivity
    }

    fun setCurrentActivity(activityID : Int) {
        currentActivity = activityID
    }


    // Requests

    // Create New Activity
    fun createActivity(activity : CreateActivitySerializable, token : String) {
        _activityCreateUIState.value = ActivityStartUIState.Loading

        viewModelScope.launch {
            val response = activityService.postCreateActivity(activity, token)
            if(response.message.substring(0,3) == "200"){
                _activityCreateUIState.value = ActivityStartUIState.Success(response.id)
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

    // Get Garbage in Activity
    fun getGarbageInActivity(currentActivity : Int, token: String) {
        _garbageInActivityUIState.value = GarbageInActivityUIState.Loading

        viewModelScope.launch {
            val response = activityService.getGarbageInActivity(currentActivity, token)
            if(response.message.substring(0,3) == "200"){
                _garbageInActivityUIState.value = GarbageInActivityUIState.Success(response.data)
            } else {
                _garbageInActivityUIState.value = GarbageInActivityUIState.Error(response.message)
            }
        }
    }

    // PATCH Garbage in Activity
    fun patchGarbageInActivity(currentActivity: Long, garbageID : Long, garbageAmountDTO: GarbageAmountDTO, token: String) {
        _updateGarbageInActivityUIState.value = UpdateGarbageInActivityUIState.Loading

        viewModelScope.launch {
            val response = activityService.patchGarbageInActivity(currentActivity, garbageID, garbageAmountDTO, token)
            if(response.message.substring(0,3) == "200"){
                _updateGarbageInActivityUIState.value = UpdateGarbageInActivityUIState.Success
            } else {
                _updateGarbageInActivityUIState.value = UpdateGarbageInActivityUIState.Error(response.message)
            }
        }
    }

    // POST Garbage in Activity
    fun postGarbageInActivity(garbageInActivityDTO: GarbageInActivityDTO, currentActivity : Long, token: String) {
        _addGarbageInActivityUIState.value = AddGarbageInActivityUIState.Loading

        viewModelScope.launch {
            val response = activityService.postGarbageInActivity(garbageInActivityDTO, token, currentActivity)
            if(response.message.substring(0,3) == "200"){
                _addGarbageInActivityUIState.value = AddGarbageInActivityUIState.Success(response.data)
            } else {
                _addGarbageInActivityUIState.value = AddGarbageInActivityUIState.Error(response.message)
            }
        }
    }

    // Delete Garbage in Activity
    fun deleteGarbageInActivity(garbage_to_delete : Long, token: String) {
        _deleteGarbageInActivity.value = DeleteGarbageInActivityUIState.Loading

        viewModelScope.launch {
            val response = activityService.deleteGarbageInActivity(garbage_to_delete, token)
            if(response.message.substring(0,3) == "200"){
                _deleteGarbageInActivity.value = DeleteGarbageInActivityUIState.Success
            } else {
                _deleteGarbageInActivity.value = DeleteGarbageInActivityUIState.Error(response.message)
            }
        }
    }
}