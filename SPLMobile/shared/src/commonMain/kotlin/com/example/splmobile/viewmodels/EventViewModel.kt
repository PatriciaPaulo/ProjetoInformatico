package com.example.splmobile.viewmodels

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.equipments.EquipmentDTO
import com.example.splmobile.dtos.equipments.EquipmentInEventDTO
import com.example.splmobile.dtos.events.EventDTO
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
        data class Success(val events: List<EventDTO>) : EventsUIState()
        data class Error(val error: String) : EventsUIState()
        object Loading : EventsUIState()
        object Empty : EventsUIState()
    }

    //state get event id
    private val _eventByIdUIState = MutableStateFlow<EventByIdUIState>(EventByIdUIState.Empty)
    val eventByIdUIState = _eventByIdUIState.asStateFlow()
    sealed class EventByIdUIState {
        data class Success(val event: EventDTO) : EventByIdUIState()
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

    //state UPDATE event
    private val _eventUpdateUIState = MutableStateFlow<EventUpdateUIState>(EventUpdateUIState.Empty)
    val eventUpdateUIState = _eventUpdateUIState.asStateFlow()
    sealed class EventUpdateUIState {
        object UpdateStatusSuccess: EventUpdateUIState()
        object UpdateSuccess: EventUpdateUIState()
        data class Error(val error: String) : EventUpdateUIState()
        object Loading : EventUpdateUIState()
        object Empty : EventUpdateUIState()
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
    fun getEventsByID(eventoId: Long) {
        _eventByIdUIState.value = EventByIdUIState.Loading
        log.v("getting all events ")
        viewModelScope.launch {
            val response = eventService.getEventsById(eventoId)

            if(response.message.substring(0,3)  == "200"){
                log.v("getting all  ${response.data}")
                _eventByIdUIState.value = EventByIdUIState.Success(response.data)
            }else{
                _eventByIdUIState.value = EventByIdUIState.Error(response.message)
            }
        }
    }
    fun createEvent(event: EventDTO, garbageSpots :List<Long>,garbageType : List<Long>, equipments : List<EquipmentInEventDTO>,token: String) {
        log.v("creating event $event")
        _eventCreateUIState.value = EventCreateUIState.Loading
        viewModelScope.launch {
            val response_event = eventService.postEvent(EventRequest(event,garbageType,garbageSpots,equipments),token)
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
    fun updateEventStatus(eventId: Long, status: String, token: String) {
        log.v("update event $eventId status")
        _eventUpdateUIState.value = EventUpdateUIState.Loading
        viewModelScope.launch {
            val response = eventService.patchEventStatus(eventId,status,token)
            if(response.message.substring(0,3)  == "200"){
                log.v("Creating event successful")
                _eventUpdateUIState.value = EventUpdateUIState.UpdateStatusSuccess
                getEvents()
            }else{
                log.v("Creating event error")
                _eventUpdateUIState.value = EventUpdateUIState.Error(response.message)
            }
        }
    }

    fun updateEvent(eventId: Long, event: EventDTO, garbageSpots :List<Long>,garbageType : List<Long>,equipments : List<EquipmentInEventDTO>, token: String) {
        log.v("update event $eventId status")
        _eventUpdateUIState.value = EventUpdateUIState.Loading
        viewModelScope.launch {
            val response = eventService.putEvent(eventId,EventRequest(event,garbageType,garbageSpots,equipments),token)
            if(response.message.substring(0,3)  == "200"){
                log.v("Creating event successful")
                _eventUpdateUIState.value = EventUpdateUIState.UpdateSuccess
                getEventsByID(eventId)
            }else{
                log.v("Creating event error")
                _eventUpdateUIState.value = EventUpdateUIState.Error(response.message)
            }
        }
    }

    //state get equiment
    private val _equipmentUIState = MutableStateFlow<EquipmentUIState>(EquipmentUIState.Empty)
    val equipmentUIState = _equipmentUIState.asStateFlow()
    sealed class EquipmentUIState {
        data class Success(val equipments: List<EquipmentDTO>) : EquipmentUIState()
        data class Error(val error: String) : EquipmentUIState()
        object Loading : EquipmentUIState()
        object Empty : EquipmentUIState()
    }

    //garbage type section
    fun getEquipments(token: String) {
        _equipmentUIState.value = EquipmentUIState.Loading
        log.v("getting all EQUIMENTS")
        viewModelScope.launch {
            val response = eventService.getEquipments(token)

            if(response.message.substring(0,3)  == "200"){
                _equipmentUIState.value = EquipmentUIState.Success(response.data)
            }else{
                _equipmentUIState.value = EquipmentUIState.Error(response.message)
            }
        }

    }

}
