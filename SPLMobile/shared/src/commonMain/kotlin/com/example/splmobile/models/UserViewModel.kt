package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.objects.events.UserInEventDTO
import com.example.splmobile.objects.users.UserDTO
import com.example.splmobile.services.userInEvent.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UserViewModel (
    private val userService: UserService,
    private val eventViewModel: EventViewModel,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("UserInEventViewModel")



    //state participate in event
    private val _eventParticipateUIState = MutableStateFlow<EventParticipateUIState>(EventParticipateUIState.Empty)
    val eventParticipateUIState = _eventParticipateUIState.asStateFlow()
    sealed class EventParticipateUIState {
        data class SuccessParticipate(val userID: Long): EventParticipateUIState()
        data class SuccessUpdate(val userID: Long): EventParticipateUIState()
        data class SuccessOrganizer(val userID: Long): EventParticipateUIState()
        data class Error(val error: String) : EventParticipateUIState()
        object Loading : EventParticipateUIState()
        object Empty : EventParticipateUIState()


    }
    //state get users in event
    private val _usersInEventUIState = MutableStateFlow<UsersInEventUIState>(UsersInEventUIState.Empty)
    val usersInEventUIState = _usersInEventUIState.asStateFlow()
    sealed class UsersInEventUIState {
        data class Success(val user_events: List<UserInEventDTO>) : UsersInEventUIState()
        data class Error(val error: String) : UsersInEventUIState()
        object Loading : UsersInEventUIState()
        object Empty : UsersInEventUIState()
    }

    //state get all users
    private val _usersUIState = MutableStateFlow<UsersUIState>(UsersUIState.Empty)
    val usersUIState = _usersUIState.asStateFlow()
    sealed class UsersUIState {
        data class SuccessUsers(val users: List<UserDTO>) : UsersUIState()
        data class SuccessUser(val user: UserDTO) : UsersUIState()
        data class Error(val error: String) : UsersUIState()
        object Loading : UsersUIState()
        object Empty : UsersUIState()
    }

    fun getUsersInEvent(eventId: Long,token: String) {
        _usersInEventUIState.value = UsersInEventUIState.Loading
        log.v("getting all users in event ")
        viewModelScope.launch {
            val response = userService.getUsersInEvent(eventId,token)

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _usersInEventUIState.value = UsersInEventUIState.Success(response.data)
            }else{
                _usersInEventUIState.value = UsersInEventUIState.Error(response.message)
            }
        }

    }
    fun getAllUsers(token: String) {
        _usersUIState.value = UsersUIState.Loading

        log.v("getting all users in event ")
        viewModelScope.launch {
            val response = userService.getAllUsersStats(token)

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _usersUIState.value = UsersUIState.SuccessUsers(response.data)
            }else{
                _usersUIState.value = UsersUIState.Error(response.message)
            }
        }

    }

    fun getUserStats(userID: Long,token: String) {
        _usersUIState.value = UsersUIState.Loading
        log.v("getting all users in event ")
        viewModelScope.launch {
            val response = userService.getUserStats(userID,token)

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _usersUIState.value = UsersUIState.SuccessUser(response.data)
            }else{
                _usersUIState.value = UsersUIState.Error(response.message)
            }
        }

    }



    fun participateInEvent(eventID: Long,token: String) {
        log.v("signing up in event $eventID")
        _eventParticipateUIState.value = EventParticipateUIState.Loading
        viewModelScope.launch {
            val response = userService.postParticipateInEvent(eventID,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("signing up in event successful")
                _eventParticipateUIState.value = EventParticipateUIState.SuccessParticipate(response.data.toLong())

            }else{
                log.v("signing up in event error")
                _eventParticipateUIState.value = EventParticipateUIState.Error(response.message)
            }
        }
    }

    fun participateInEventOrganizer(eventID: Long,userID: Long,token: String) {
        log.v("signing up in event $eventID")
        _eventParticipateUIState.value = EventParticipateUIState.Loading
        viewModelScope.launch {
            val response = userService.postParticipateInEventByOrganizer(eventID,userID,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("signing up in event successful")
                _eventParticipateUIState.value = EventParticipateUIState.SuccessOrganizer(response.data.toLong())

            }else{
                log.v("signing up in event error")
                _eventParticipateUIState.value = EventParticipateUIState.Error(response.message)
            }
        }
    }

    fun participateStatusUpdateInEvent(eventID: Long,userEventID: Long,status: String,token: String) {
        log.v("update user in event $eventID")
        _eventParticipateUIState.value = EventParticipateUIState.Loading
        viewModelScope.launch {
            val response = userService.patchStatusParticipateInEvent(eventID,userEventID,status,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("Creating event successful")
                _eventParticipateUIState.value = EventParticipateUIState.SuccessUpdate(response.data.toLong())
                //todo change to being called in UI if time
                eventViewModel.getEvents()
            }else{
                log.v("Creating event error")
                _eventParticipateUIState.value = EventParticipateUIState.Error(response.message)
            }
        }
    }




}