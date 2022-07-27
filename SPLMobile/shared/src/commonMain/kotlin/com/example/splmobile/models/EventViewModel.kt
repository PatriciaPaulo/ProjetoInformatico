package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.events.EventSerializable
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


    //state create event
    private val _eventCreateUIState = MutableStateFlow<EventCreateUIState>(EventCreateUIState.Empty)
    val eventCreateUIState = _eventCreateUIState.asStateFlow()
    sealed class EventCreateUIState {
        object Success: EventCreateUIState()
        data class Error(val error: String) : EventCreateUIState()
        object Loading : EventCreateUIState()

        object Empty : EventCreateUIState()
    }


    fun getEvents() {
        _eventsUIState.value = EventsUIState.Loading
        log.v("getting all garbage spot ")
        viewModelScope.launch {
            val response = eventService.getEvents()

            if(response.message.substring(0,3)  == "200"){
                _eventsUIState.value = EventsUIState.Success(response.data)
            }else{
                _eventsUIState.value = EventsUIState.Error(response.message)
            }
        }

    }
    fun createEvent(event: EventSerializable, token: String) {
        log.v("creating garbage spot $event")
        _eventCreateUIState.value = EventCreateUIState.Loading
        viewModelScope.launch {
            val response = eventService.postEvent(event,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("Creating garbage spot successful")
                _eventCreateUIState.value = EventCreateUIState.Success
                getEvents()
            }else{
                log.v("Creating garbage spot error")
                _eventCreateUIState.value = EventCreateUIState.Error(response.message)
            }
        }

    }
}
