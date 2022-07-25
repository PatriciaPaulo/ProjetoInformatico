package com.example.splmobile.services.userInEvent

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestDataResponse
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.*
import com.example.splmobile.dtos.users.UserSerializable
import com.example.splmobile.dtos.users.UserStatsResponse
import com.example.splmobile.dtos.users.UsersStatsResponse
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

class UserServiceImpl (
    private val log: Logger, engine: HttpClientEngine
) : UserService {
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

    init {
        ensureNeverFrozen()
    }


    override suspend fun postParticipateInEvent(
        eventID: Long,
        token: String
    ): RequestDataResponse {
        log.d { "post participate in event" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }

                url("api/events/"+eventID+"/signUpEvent")
            }.body() as RequestDataResponse
        }
        catch (ex :Exception){
            return RequestDataResponse("","$ex")
        }
    }

    override suspend fun postParticipateInEventByOrganizer(
        eventID: Long,
        userID: Long,
        token: String
    ): RequestDataResponse {
        log.d { "post participate in event" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(userID)
                url("api/events/"+eventID+"/signUpEventOrganizer")
            }.body() as RequestDataResponse
        }
        catch (ex :Exception){
            return RequestDataResponse("","$ex")
        }
    }

    override suspend fun patchStatusParticipateInEvent(
        eventId: Long,
        user_eventId: Long,
        status: String,
        token: String
    ): RequestDataResponse {
        log.d { "post participate in event" }
        try{
            return client.patch {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(status)
                url("api/events/"+eventId+"/signUpUpdateStatusEvent/"+user_eventId)
            }.body() as RequestDataResponse
        }
        catch (ex :Exception){
            return RequestDataResponse("","$ex")
        }
    }




    override suspend fun getUsersInEvent(user_eventId: Long, token: String): UserInEventResponse {
        log.d { "Fetching events from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/events/"+ user_eventId +"/usersinevent")
            }.body() as UserInEventResponse
        }catch (ex :Exception){
            return UserInEventResponse(emptyList(),"$ex")
        }


    }


    override suspend fun getAllUsersStats(token: String): UsersStatsResponse {
        log.d { "Fetching users from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/usersStats")
            }.body() as UsersStatsResponse
        }catch (ex :Exception){
            return UsersStatsResponse(emptyList(),"$ex")
        }
    }

    override suspend fun getUserStats(userID: Long, token: String): UserStatsResponse {
        log.d { "Fetching users from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/usersStats/"+userID)
            }.body() as UserStatsResponse
        }catch (ex :Exception){
            return UserStatsResponse(UserSerializable(0,"","","","",""),"$ex")
        }
    }

    override suspend fun postUserInEvent(user_eventID: Long, userID: Long): RequestMessageResponse {
        TODO("Not yet implemented")
    }


    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }
}