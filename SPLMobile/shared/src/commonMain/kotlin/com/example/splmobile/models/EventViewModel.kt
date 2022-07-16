package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.events.EventRequest
import com.example.splmobile.services.events.EventService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventViewModel (
    private val eventService: EventService,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("EventViewModel")

    //state get all eventos
    private val _eventsUIState = MutableStateFlow<EventsUIState>(EventsUIState.Empty)
    val eventsUIState = _eventsUIState.asStateFlow()
    sealed class EventsUIState {
        data class Success(val events: List<EventSerializable>) : EventsUIState()
        data class Error(val error: String) : EventsUIState()
        object Loading : EventsUIState()
        object Empty : EventsUIState()
    }
    //state get event id
    private val _eventByIdUIState = MutableStateFlow<EventByIdUIState>(EventByIdUIState.Empty)
    val eventByIdUIState = _eventByIdUIState.asStateFlow()
    sealed class EventByIdUIState {
        data class Success(val event: EventSerializable) : EventByIdUIState()
        data class Error(val error: String) : EventByIdUIState()
        object Loading : EventByIdUIState()
        object Empty : EventByIdUIState()
    }


    //state create event
    private val _eventCreateUIState = MutableStateFlow<EventCreateUIState>(EventCreateUIState.Empty)
    val eventCreateUIState = _eventCreateUIState.asStateFlow()
    sealed class EventCreateUIState {
        object Success: EventCreateUIState()
        data class Error(val error: String) : EventCreateUIState()
        object Loading : EventCreateUIState()

        object Empty : EventCreateUIState()
    }

    //state participate in event
    private val _eventParticipateUIState = MutableStateFlow<EventParticipateUIState>(EventParticipateUIState.Empty)
    val eventParticipateUIState = _eventParticipateUIState.asStateFlow()
    sealed class EventParticipateUIState {
        object SuccessParticipate: EventParticipateUIState()
        object SuccessUpdate: EventParticipateUIState()
        data class Error(val error: String) : EventParticipateUIState()
        object Loading : EventParticipateUIState()
        object Empty : EventParticipateUIState()
    }



    fun getEvents() {
        _eventsUIState.value = EventsUIState.Loading
        log.v("getting all events ")
        viewModelScope.launch {
            val response = eventService.getEvents()

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _eventsUIState.value = EventsUIState.Success(response.data)
            }else{
                _eventsUIState.value = EventsUIState.Error(response.message)
            }
        }

    }
    fun getEventsByID(eventoId: String) {
        _eventByIdUIState.value = EventByIdUIState.Loading
        log.v("getting all events ")
        viewModelScope.launch {
            val response = eventService.getEventsById(eventoId.toLong())

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _eventByIdUIState.value = EventByIdUIState.Success(response.data)
            }else{
                _eventByIdUIState.value = EventByIdUIState.Error(response.message)
            }
        }
    }
    fun createEvent(event: EventSerializable, garbageType : List<Long>, token: String) {
        log.v("creating event $event")
        _eventCreateUIState.value = EventCreateUIState.Loading
        viewModelScope.launch {
            val response_event = eventService.postEvent(EventRequest(event,garbageType),token)
            //val response_garbage_in_event = eventService.postGarbageTypeInEvent(GarbageInEventRequest(garbageType,event.id),token)
            if(response_event.message.substring(0,3)  == "200" ){
                log.v("Creating event successful")
                _eventCreateUIState.value = EventCreateUIState.Success
                getEvents()
            }else{
                log.v("Creating event error")
                _eventCreateUIState.value = EventCreateUIState.Error(response_event.message )
            }
        }

    }

    fun participateInEvent(eventID: Long,token: String) {
        log.v("signing up in event $eventID")
        _eventParticipateUIState.value = EventParticipateUIState.Loading
        viewModelScope.launch {
            val response = eventService.postParticipateInEvent(eventID,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("signing up in event successful")
                _eventParticipateUIState.value = EventParticipateUIState.SuccessParticipate
                getEvents()
            }else{
                log.v("signing up in event error")
                _eventParticipateUIState.value = EventParticipateUIState.Error(response.message)
            }
        }
    }
    fun participateStatusUpdateInEvent(eventID: Long,userEventID: Long,status: String,token: String) {
        log.v("signing up in event $eventID")
        _eventParticipateUIState.value = EventParticipateUIState.Loading
        viewModelScope.launch {
            val response = eventService.patchStatusParticipateInEvent(eventID,userEventID,status,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("Creating event successful")
                _eventParticipateUIState.value = EventParticipateUIState.SuccessUpdate
                getEvents()
            }else{
                log.v("Creating event error")
                _eventParticipateUIState.value = EventParticipateUIState.Error(response.message)
            }
        }
    }


}
