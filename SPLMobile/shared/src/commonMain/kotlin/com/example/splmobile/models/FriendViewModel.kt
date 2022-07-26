package com.example.splmobile.models

import co.touchlab.kermit.Logger
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
        object Success: FriendRequestUIState()
        data class Error(val error: String) : FriendRequestUIState()
        object Loading : FriendRequestUIState()
        object Empty : FriendRequestUIState()
    }


    fun sendFrendRequest(userID: Long, token: String) {
        log.v("sendFrendRequest to $userID")
        _friendRequestUIState.value = FriendRequestUIState.Loading
        viewModelScope.launch {
            val response_event = friendService.postFriendRequest(userID,token)
            //val response_garbage_in_event = eventService.postGarbageTypeInEvent(GarbageInEventRequest(garbageType,event.id),token)
            if(response_event.message.substring(0,3)  == "200" ){
                log.v("Creating event successful")
                _friendRequestUIState.value = FriendRequestUIState.Success

            }else{
                log.v("Creating event error")
                _friendRequestUIState.value = FriendRequestUIState.Error(response_event.message )
            }
        }

    }
}

