package com.example.splmobile.models

import UserInfoService
import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.activities.ActivitySerializable
import com.example.splmobile.dtos.events.UserInEventSerializable
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UserSerializable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel (
    private val userInfoService: UserInfoService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("UserInfoViewModel")

    // my id
    private val _myIDUIState = MutableStateFlow<Long>(0L)
    val myIdUIState = _myIDUIState.asStateFlow()

    //state get me user
    private val _myInfoUserUIState = MutableStateFlow<MyInfoUserUIState>(MyInfoUserUIState.Empty)
    val myInfoUserUIState = _myInfoUserUIState.asStateFlow()
    sealed class MyInfoUserUIState {
        data class Success(val data: UserSerializable) : MyInfoUserUIState()
        data class Error(val error: String) : MyInfoUserUIState()
        object Loading : MyInfoUserUIState()
        object Offline : MyInfoUserUIState()
        object Empty : MyInfoUserUIState()
    }

    //state put me user
    private val _myInfoUserUpdateUIState = MutableStateFlow<MyInfoUserUpdateUIState>(
        MyInfoUserUpdateUIState.Empty
    )
    val myInfoUserUpdateUIState = _myInfoUserUpdateUIState.asStateFlow()
    sealed class MyInfoUserUpdateUIState {
        data class Success(val data: UserSerializable) : MyInfoUserUpdateUIState()
        data class Error(val error: String) : MyInfoUserUpdateUIState()
        object Loading : MyInfoUserUpdateUIState()
        object Offline : MyInfoUserUpdateUIState()
        object Empty : MyInfoUserUpdateUIState()
    }

    //state get my events
    private val _myEventsUIState = MutableStateFlow<MyEventsUIState>(MyEventsUIState.Empty)
    val myEventsUIState = _myEventsUIState.asStateFlow()
    sealed class MyEventsUIState {
        data class Success(val events: List<UserInEventSerializable>) : MyEventsUIState()
        data class Error(val error: String) : MyEventsUIState()
        object Loading : MyEventsUIState()
        object Empty : MyEventsUIState()
    }

    //state get my activities
    private val _myActivitiesUIState = MutableStateFlow<MyActivitiesUIState>(MyActivitiesUIState.Empty)
    val myActivitiesUIState = _myActivitiesUIState.asStateFlow()
    sealed class MyActivitiesUIState {
        data class Success(val activities: List<ActivitySerializable>) : MyActivitiesUIState()
        data class SuccessLast5(val activities: List<ActivitySerializable>) : MyActivitiesUIState()
        data class Error(val error: String) : MyActivitiesUIState()
        object Loading : MyActivitiesUIState()
        object Empty : MyActivitiesUIState()
    }



    fun getMyInfo(token: String) {
        _myInfoUserUIState.value = MyInfoUserUIState.Loading
        log.v("Getting All User Info ")
        viewModelScope.launch {
            val response = userInfoService.getUser(token)

            if(response.message.substring(0,3) == "200"){
                _myInfoUserUIState.value = MyInfoUserUIState.Success(response.data)
                log.v("Getting All User Info ID --- ${response.data.id}")
                _myIDUIState.value = response.data.id
            }else{
                _myInfoUserUIState.value = MyInfoUserUIState.Error(response.message)

            }

        }

    }
    fun putMyInfo(user :UserSerializable, token: String) {
        _myInfoUserUpdateUIState.value = MyInfoUserUpdateUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = userInfoService.putUser(user,token)

            if(response.message.substring(0,3)  == "200"){
                _myInfoUserUpdateUIState.value = MyInfoUserUpdateUIState.Success(response.data)
            }else{
                _myInfoUserUpdateUIState.value = MyInfoUserUpdateUIState.Error(response.message)
            }


        }

    }

    fun getMyEvents(token: String) {
        _myEventsUIState.value = MyEventsUIState.Loading
        log.v("getting all my events")
        viewModelScope.launch {
            val response = userInfoService.getMyEvents(token)

            if(response.message.substring(0,3)  == "200"){
                log.v("getting ${response.data}")
                _myEventsUIState.value = MyEventsUIState.Success(response.data)
            }else{
                _myEventsUIState.value = MyEventsUIState.Error(response.message)
            }
        }
    }
    fun getMyActivities(token: String) {
        _myActivitiesUIState.value = MyActivitiesUIState.Loading
        log.v("getting all my activities")
        viewModelScope.launch {
            val response = userInfoService.getMyActivities(token)

            if(response.message.substring(0,3) == "200"){
                _myActivitiesUIState.value = MyActivitiesUIState.Success(response.data)
                _myActivitiesUIState.value =
                    MyActivitiesUIState.SuccessLast5(response.data.takeLast(5))
            }else{
                _myActivitiesUIState.value = MyActivitiesUIState.Error(response.message)
            }
        }

    }

    // Check Email
    fun checkEmail(email: String) = viewModelScope.launch {
        _myInfoUserUIState.value = MyInfoUserUIState.Loading

        var emailResponse : EmailCheckResponse = viewModelScope.async() {
            log.v { "getEmailExists" }
            try {
                userInfoService.checkMyEmail(EmailRequest(email))
            } catch (exception : Exception) {

            }
        }.await() as EmailCheckResponse
    }


}
