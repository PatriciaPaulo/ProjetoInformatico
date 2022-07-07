package com.example.splmobile.models.userInfo

import UserInfoService
import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UserSerializable
import com.example.splmobile.models.ViewModel
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
    private val _myInfoUserUpdateUIState = MutableStateFlow<MyInfoUserUpdateUIState>(MyInfoUserUpdateUIState.Empty)
    val myInfoUserUpdateUIState = _myInfoUserUpdateUIState.asStateFlow()
    sealed class MyInfoUserUpdateUIState {
        data class Success(val data: UserSerializable) : MyInfoUserUpdateUIState()
        data class Error(val error: String) : MyInfoUserUpdateUIState()
        object Loading : MyInfoUserUpdateUIState()
        object Offline : MyInfoUserUpdateUIState()
        object Empty : MyInfoUserUpdateUIState()
    }

    //state get my locais lixo
    private val _myGarbageSpotsUIState = MutableStateFlow<MyGarbageSpotsUIState>(MyGarbageSpotsUIState.Empty)
    val myGarbageSpotsUIState = _myGarbageSpotsUIState.asStateFlow()
    sealed class MyGarbageSpotsUIState {
        data class Success(val garbageSpots: List<GarbageSpotSerializable>) : MyGarbageSpotsUIState()
        data class Error(val error: String) : MyGarbageSpotsUIState()
        object Loading : MyGarbageSpotsUIState()
        object Offline : MyGarbageSpotsUIState()
        object Empty : MyGarbageSpotsUIState()
    }



    fun getMyInfo(token: String) {
        _myInfoUserUIState.value = MyInfoUserUIState.Loading
        log.v("Getting All User Info ")
        viewModelScope.launch {
            val response = userInfoService.getUser(token)

            if(response.status == "200"){
                _myInfoUserUIState.value = MyInfoUserUIState.Success(response.data)
                log.v("Getting All User Info ID --- ${response.data.id}")
                _myIDUIState.value = response.data.id
            }else{
                if(response.status == "500"){
                    _myInfoUserUIState.value =MyInfoUserUIState.Offline
                }
                else{
                    _myInfoUserUIState.value =MyInfoUserUIState.Error(response.status)
                }
            }

        }

    }
    fun putMyInfo(user :UserSerializable, token: String) {
        _myInfoUserUpdateUIState.value = MyInfoUserUpdateUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = userInfoService.putUser(user,token)

            if(response.status == "200"){
                _myInfoUserUpdateUIState.value = MyInfoUserUpdateUIState.Success(response.data)
            }else{
                if(response.status == "500"){
                    _myInfoUserUpdateUIState.value =MyInfoUserUpdateUIState.Offline
                }
                else{
                    _myInfoUserUpdateUIState.value =MyInfoUserUpdateUIState.Error(response.status)
                }
            }

        }

    }


    fun getMyGarbageSpots(token: String) {
        _myGarbageSpotsUIState.value = MyGarbageSpotsUIState.Loading
        log.v("getting all my local lixo ")
        viewModelScope.launch {
            val response = userInfoService.getMyGarbageSpots(token)

            if(response.status == "200"){
                _myGarbageSpotsUIState.value = MyGarbageSpotsUIState.Success(response.data)
            }else{
                _myGarbageSpotsUIState.value =MyGarbageSpotsUIState.Error(response.status)
            }
        }

    }

    // Check Email
    fun checkEmail(email: String) = viewModelScope.launch {
        _myInfoUserUIState.value = UserInfoViewModel.MyInfoUserUIState.Loading

        var emailResponse : EmailCheckResponse = viewModelScope.async() {
            log.v { "getEmailExists" }
            try {
                userInfoService.checkMyEmail(EmailRequest(email))
            } catch (exception : Exception) {

            }
        }.await() as EmailCheckResponse
    }


}
