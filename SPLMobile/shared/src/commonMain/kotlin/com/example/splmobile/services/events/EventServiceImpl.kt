package com.example.splmobile.services.events

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class EventServiceImpl (
    private val log: Logger, engine: HttpClientEngine
) : EventService {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    log.v { message + "API IMP MESSAGE" }
                }
            }

            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }


    }
    private val emptyEvent = EventDTO(0,"","","","","","","","","","","","", emptyList(), emptyList())

    init {
        ensureNeverFrozen()
    }

    override suspend fun getEvents(): EventsResponse {
        log.d { "Fetching events from network" }
        try{
            return client.get {
                contentType(ContentType.Application.Json)
                url("api/events")
            }.body() as EventsResponse
        }catch (ex :Exception){
            return EventsResponse(emptyList(),"$ex")
        }

    }

    override suspend fun getEventsById(eventId: Long): EventResponse {
        log.d { "Fetching event by id from network" }
        try{
            return client.get {
                contentType(ContentType.Application.Json)
                url("api/events/"+eventId)
            }.body() as EventResponse
        }catch (ex :Exception){
            return EventResponse(emptyEvent,"$ex")
        }
    }

    override suspend fun postEvent(
        eventRequest: EventRequest,
        token: String
    ): RequestMessageResponse {
        log.d { "post new event" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(eventRequest)
                url("api/events")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("${ex.message}")
        }


    }

    override suspend fun putEvent(
        eventId: Long,
        event: EventDTO,
        token: String
    ): RequestMessageResponse {
        log.d { "put event" }
        try{
            return client.put {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(event)
                url("api/events/"+eventId)
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }

    override suspend fun patchEventStatus(
        eventId: Long,
        status: String,
        token: String
    ): RequestMessageResponse {
        log.d { "patch event status" }
        try{
            return client.patch {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(status)
                url("api/events/"+eventId+"/updateStatus")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }
}