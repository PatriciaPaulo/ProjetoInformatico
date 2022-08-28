package com.example.splmobile.viewmodels

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.events.EventDTO
import com.example.splmobile.dtos.messages.EventMessageRequest
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.dtos.myInfo.UserSerializable
import com.example.splmobile.services.messages.MessageService
import com.example.splmobile.services.websockets.MessageWebsocket
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
        data class SuccessIndividual(val userID: Long) : NotificationUIState()
        data class SuccessEvent(val eventID: Long) : NotificationUIState()
        data class SuccessFriendRequestReceived(val user: UserSerializable) : NotificationUIState()
        data class SuccessEventStatus(val event: EventDTO) : NotificationUIState()
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
    fun sendEventMessage(message : EventMessageRequest, token: String) {
        log.v("send message to  ${message.eventID}")
        _messageCreateUIState.value = MessageCreateUIState.Loading
        viewModelScope.launch {
            val response = messageService.postEventMessage(message,token)
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
        data class Success(val message: List<MessageDTO>) : LastMessageUIState()
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

    fun getLastMessage(token: String) {
        _lastMessageUIState.value = LastMessageUIState.Loading
        log.v("getting all message with friends")
        viewModelScope.launch {
            val response = messageService.getLastMessage(token)

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
        log.v("message individual notification ")
        _notiReceivedUIState.value = NotificationUIState.SuccessIndividual(userID)
    }

    fun messageNotificationEvent(eventID: Long) {
        log.v("message event notification ")
        _notiReceivedUIState.value = NotificationUIState.SuccessEvent(eventID)
    }


    fun getLastEventMessage(token: String) {
        _lastMessageUIState.value = LastMessageUIState.Loading
        log.v("getting all message in event")
        viewModelScope.launch {
            val response = messageService.getLastEventMessage(token)

            if(response.message.substring(0,3)  == "200"){
                _lastMessageUIState.value = LastMessageUIState.Success(response.data)
            }else{
                _lastMessageUIState.value = LastMessageUIState.Error(response.message)
            }
        }
    }

    fun notificationFriendRequest(user: UserSerializable) {
        log.v("Notification Friend Request Received")
        _notiReceivedUIState.value = NotificationUIState.SuccessFriendRequestReceived(user)
    }


    fun notificationEventStatus(event: EventDTO) {
        log.v("Notification SuccessEventStatus Received")
        _notiReceivedUIState.value = NotificationUIState.SuccessEventStatus(event)
    }
}
