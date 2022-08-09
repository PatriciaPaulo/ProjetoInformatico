package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.services.messages.MessageService
import com.example.splmobile.websockets.MessageWebsocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MessageViewModel (
    private val messageService: MessageService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("MessageViewModel")
    private val websocket : MessageWebsocket = MessageWebsocket(log,this)


    fun startConnection(token:String) {
        log.v("connecting websocket ")
        _notiReceivedUIState.value = NotificationUIState.Loading
        websocket.connect(token)
    }
    //state Notificatio Received
    private val _notiReceivedUIState = MutableStateFlow<NotificationUIState>(
        NotificationUIState.Empty
    )
    val notiReceivedUIState = _notiReceivedUIState.asStateFlow()
    sealed class NotificationUIState {
        data class Success(val userID: Long) : NotificationUIState()
        data class Error(val error: String) : NotificationUIState()
        object Loading : NotificationUIState()
        object Offline : NotificationUIState()
        object Empty : NotificationUIState()
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
    private val _messagesUIState = MutableStateFlow<MessagesUIState>(MessagesUIState.Empty)
    val messagesUIState = _messagesUIState.asStateFlow()


    sealed class MessagesUIState {
        data class Success(val messages: List<MessageDTO>) : MessagesUIState()
        data class Error(val error: String) : MessagesUIState()
        object Loading : MessagesUIState()
        object Empty : MessagesUIState()
    }
    //state get last message with user
    private val _lastMessageUIState = MutableStateFlow<LastMessageUIState>(LastMessageUIState.Empty)
    val lastMessageUIState = _lastMessageUIState.asStateFlow()
    sealed class LastMessageUIState {
        data class Success(val message: MessageDTO) : LastMessageUIState()
        data class Error(val error: String) : LastMessageUIState()
        object Loading : LastMessageUIState()
        object Empty : LastMessageUIState()
    }
    fun getMessages(userID:Long,token: String) {
        _messagesUIState.value = MessagesUIState.Loading
        log.v("getting all message with $userID ")
        viewModelScope.launch {
            val response = messageService.getMessages(userID,token)

            if(response.message.substring(0,3)  == "200"){
                _messagesUIState.value = MessagesUIState.Success(response.data)
            }else{
                _messagesUIState.value = MessagesUIState.Error(response.message)
            }
        }

    }  fun getEventMessages(eventID: Long,token: String) {
        _messagesUIState.value = MessagesUIState.Loading
        log.v("getting all message with $eventID ")
        viewModelScope.launch {
            val response = messageService.getEventMessages(eventID,token)

            if(response.message.substring(0,3)  == "200"){
                _messagesUIState.value = MessagesUIState.Success(response.data)
            }else{
                _messagesUIState.value = MessagesUIState.Error(response.message)
            }
        }

    }

    fun getLastMessage(userID:Long,token: String) {
        _lastMessageUIState.value = LastMessageUIState.Loading
        log.v("getting all message with $userID ")
        viewModelScope.launch {
            val response = messageService.getLastMessage(userID,token)

            if(response.message.substring(0,3)  == "200"){
                _lastMessageUIState.value = LastMessageUIState.Success(response.data)
            }else{
                _lastMessageUIState.value = LastMessageUIState.Error(response.message)
            }
        }

    }


    fun errorConnection(error:String) {
        log.v(" websocket error")
        _notiReceivedUIState.value = NotificationUIState.Error(error)
    }
    fun closeConnection() {
        log.v("closing websocket ")
        websocket.close()

    }

    fun messageNotification(userID: Long) {
        //getMessages(userID,authViewModel.tokenState.value)
        _notiReceivedUIState.value = NotificationUIState.Success(userID)
    }

    fun getLastEventMessage(eventID: Long, token: String) {
        _lastMessageUIState.value = LastMessageUIState.Loading
        log.v("getting all message with $eventID ")
        viewModelScope.launch {
            val response = messageService.getLastEventMessage(eventID,token)

            if(response.message.substring(0,3)  == "200"){
                _lastMessageUIState.value = LastMessageUIState.Success(response.data)
            }else{
                _lastMessageUIState.value = LastMessageUIState.Error(response.message)
            }
        }
    }

}
