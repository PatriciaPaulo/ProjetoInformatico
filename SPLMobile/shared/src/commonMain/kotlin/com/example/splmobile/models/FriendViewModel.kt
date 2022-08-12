package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.users.FriendDTO
import com.example.splmobile.services.friends.FriendService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FriendViewModel  (
    private val friendService: FriendService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("FriendsViewModel")

    //state friendship
    private val _friendRequestUIState = MutableStateFlow<FriendRequestUIState>(FriendRequestUIState.Empty)
    val friendRequestUIState = _friendRequestUIState.asStateFlow()
    sealed class FriendRequestUIState {
        object SuccessRequestAccepted: FriendRequestUIState()
        object SuccessRequestReceived: FriendRequestUIState()
        object SuccessRequestSent: FriendRequestUIState()
        object SuccessFriendRemoved: FriendRequestUIState()
        data class Error(val error: String) : FriendRequestUIState()
        object Loading : FriendRequestUIState()
        object Empty : FriendRequestUIState()
    }

    //state get all friendship
    private val _friendsUIState = MutableStateFlow<FriendsUIState>(FriendsUIState.Empty)
    val friendsUIState = _friendsUIState.asStateFlow()
    sealed class FriendsUIState {
        object SuccessByUserID: FriendsUIState()
        data class SuccessAll(val friends: List<FriendDTO>) : FriendsUIState()
        data class Error(val error: String) : FriendsUIState()
        object Loading : FriendsUIState()
        object Empty : FriendsUIState()
    }




    fun sendFrendRequest(userID: Long, token: String) {
        log.v("sendFrendRequest to $userID")
        _friendRequestUIState.value = FriendRequestUIState.Loading
        viewModelScope.launch {
            val response = friendService.postFriendRequest(userID, token)
            //val response_garbage_in_event = eventService.postGarbageTypeInEvent(GarbageInEventRequest(garbageType,event.id),token)
            if (response.message.substring(0, 3) == "200") {
                log.v("Creating event successful")
                _friendRequestUIState.value = FriendRequestUIState.SuccessRequestSent

            } else {
                if (response.message.substring(0, 3) == "202") {
                    log.v("Creating event successful")
                    _friendRequestUIState.value = FriendRequestUIState.SuccessRequestAccepted

                } else {
                    log.v("Creating event error")
                    _friendRequestUIState.value = FriendRequestUIState.Error(response.message)
                }
            }

        }
    }

    fun getAllFriends(token: String) {
        _friendsUIState.value = FriendsUIState.Loading
        log.v("getting all garbage spot ")
        viewModelScope.launch {
            val response = friendService.getAllFriends(token)

            if (response.message.substring(0, 3) == "200") {
                _friendsUIState.value = FriendsUIState.SuccessAll(response.data)
            } else {
                if (response.message.substring(0, 3) == "409") {
                    _friendsUIState.value = FriendsUIState.SuccessAll(response.data)
                } else {
                    _friendsUIState.value = FriendsUIState.Error(response.message)
                }
            }
        }
    }
    fun getFriendByID(userID:Long,token: String) {
        _friendsUIState.value = FriendsUIState.Loading
        log.v("getting all garbage spot ")
        viewModelScope.launch {
            val response = friendService.getFriendByID(userID,token)

            if(response.message.substring(0,3)  == "200"){
                _friendsUIState.value = FriendsUIState.SuccessByUserID
            }else{
                _friendsUIState.value = FriendsUIState.Error(response.message)
            }
        }

    }

    fun removeFriend(friendshipID: Long,token: String) {
        log.v("removeFriend $friendshipID")
        _friendRequestUIState.value = FriendRequestUIState.Loading

        viewModelScope.launch {
            val response = friendService.removeFriend(friendshipID, token)
               if (response.message.substring(0, 3) == "200") {
                log.v("removing friend successful")
                _friendRequestUIState.value = FriendRequestUIState.SuccessFriendRemoved

            } else {
                log.v("Creating event error")
                _friendRequestUIState.value = FriendRequestUIState.Error(response.message)
            }

        }
    }

    fun notificationFriendRequest() {
        _friendRequestUIState.value = FriendRequestUIState.SuccessRequestReceived

    }

}

