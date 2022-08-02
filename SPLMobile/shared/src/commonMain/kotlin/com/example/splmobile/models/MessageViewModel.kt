package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.services.messages.MessageService
import com.example.splmobile.websockets.MessageWebsocket
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MessageViewModel (
    private val messageService: MessageService,
    private val authViewModel: AuthViewModel,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("MessageViewModel")
    private val websocket : MessageWebsocket = MessageWebsocket(log,this)


    fun openConnection(token:String) {
        log.v("connecting websocket ")
        websocket.connect(token)
    }

    //state create garbage spot
    private val _messageCreateUIState = MutableStateFlow<MessageCreateUIState>(
        MessageCreateUIState.Empty
    )
    val messageCreateUIState = _messageCreateUIState.asStateFlow()
    sealed class MessageCreateUIState {
        object Success: MessageCreateUIState()
        data class Error(val error: String) : MessageCreateUIState()
        object Loading : MessageCreateUIState()
        object Empty : MessageCreateUIState()
    }


    fun sendMessage(message :IndividualMessageRequest ,token: String) {
        log.v("send message to  ${message.userID}")
        _messageCreateUIState.value = MessageCreateUIState.Loading
        viewModelScope.launch {
            val response = messageService.postMessage(message,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("sending message successful")
                _messageCreateUIState.value = MessageCreateUIState.Success

            }else{
                log.v("sending message error")
                _messageCreateUIState.value = MessageCreateUIState.Error(response.message)
            }
        }

    }

    //state get all message with user
    private val _messagessUIState = MutableStateFlow<MessagesUIState>(MessagesUIState.Empty)
    val messagesUIState = _messagessUIState.asStateFlow()


    sealed class MessagesUIState {
        data class Success(val messagess: List<MessageDTO>) : MessageViewModel.MessagesUIState()
        data class Error(val error: String) : MessageViewModel.MessagesUIState()
        object Loading : MessageViewModel.MessagesUIState()
        object Empty : MessageViewModel.MessagesUIState()
    }
    fun getMessages(userID:Long,token: String) {
        _messagessUIState.value = MessagesUIState.Loading
        log.v("getting all message with $userID ")
        viewModelScope.launch {
            val response = messageService.getMessages(userID,token)

            if(response.message.substring(0,3)  == "200"){
                _messagessUIState.value = MessagesUIState.Success(response.data)
            }else{
                _messagessUIState.value = MessagesUIState.Error(response.message)
            }
        }

    }


    fun closeConnection() {
        log.v("closing websocket ")
        websocket.close()

    }

    fun messageNotification(userID: Long) {
        getMessages(userID,authViewModel.tokenState.value)
    }

}
