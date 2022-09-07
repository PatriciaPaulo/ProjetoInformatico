package com.example.splmobile.services.activities


import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.HttpRequestUrls
import com.example.splmobile.objects.RequestMessageResponse
import com.example.splmobile.objects.activities.*
import com.example.splmobile.objects.messages.MessagesResponse
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
import co.touchlab.kermit.Logger as KermitLogger

class ActivityServiceImpl (
    private val log: KermitLogger,
    engine : HttpClientEngine
) : ActivityService {
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
            logger = object : Logger {
                override fun log(message: String) {
                    log.v { message + " ACTIVITY IMP MESSAGE" }
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

    override suspend fun postCreateActivity(
        activity: CreateActivitySerializable,
        token: String
    ): ActivityResponse {
        log.d { "Post New Activity" }
        try {
            return client.post {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(activity)
                url("api/activities")
            }.body() as ActivityResponse
        } catch (e : Exception) {
            return ActivityResponse(ActivitySerializable(0, null, null, null, null, null),"$e")
        }
    }

    override suspend fun patchActivity(
        activity: PatchActivitySerializable,
        token: String
    ): RequestMessageResponse {
        log.d { "PATCH New Activity" }

        try {
            return client.patch {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(activity)
                url("api/activities/${activity.id}")
            }.body() as RequestMessageResponse
        } catch (e : Exception) {
            return RequestMessageResponse("$e")
        }
    }

    override suspend fun getActivityTypes(): ActivitiesTypeResponse {
        log.d { "Get All Activity Types" }
        try {
            return client.get {
                contentType(ContentType.Application.Json)
                url("api/activityTypes")
            }.body() as ActivitiesTypeResponse
        } catch (e : Exception) {
            return ActivitiesTypeResponse(emptyList(), "$e")
        }
    }

    override suspend fun getGarbageInActivity(
        currentActivity: Long,
        token: String
    ): GarbageInActivityResponse {
        log.d { "Get All Garbage in Activity $currentActivity" }

        try {
            return client.get {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                url("api/activitygarbage/$currentActivity")
            }.body() as GarbageInActivityResponse
        } catch (e : ClientRequestException) {
            return GarbageInActivityResponse(emptyList(), e.message)
        }
    }

    override suspend fun patchGarbageInActivity(
        currentActivity: Long,
        garbageInActivityID: Long,
        garbage: GarbageAmountDTO,
        token: String
    ): MessagesResponse {
        log.d { "PATCH Garbage in Activity" }

        try {
            return client.patch {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(garbage)
                url("api/activitygarbage/$currentActivity/update/$garbageInActivityID")
            }.body()
        } catch (e : ClientRequestException) {
            return MessagesResponse(emptyList(), "$e")
        }
    }

    override suspend fun postGarbageInActivity(
        garbage: GarbageInActivityDTO,
        token: String,
        activityID: Long
    ): AddGarbageInActivityResponse {
        log.d { "POST Garbage in Activity" }

        try {
            return client.post {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(garbage)
                url("api/activitygarbage/$activityID/create")
            }.body()
        } catch (e : ClientRequestException) {
            return AddGarbageInActivityResponse(GarbageInActivityDTO(0, 0, 0, 0f, 0), "$e")
        }
    }

    override suspend fun deleteGarbageInActivity(
        garbageToDelete: Long,
        token: String
    ): RequestMessageResponse {
        log.d { "DELETE Garbage in Activity" }
        try {
            return client.delete {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                url("api/activitygarbage/$garbageToDelete/delete")
            }.body()
    } catch (e : Exception) {
            return RequestMessageResponse( "$e")
        }
    }

    override suspend fun getLastActivity(token: String): ActivityResponse {
        log.d { "GET Last Activity" }
        try {
            return client.get {
                if (token.isNotEmpty()) {
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                url("api/activities/last")
            }.body()
        } catch (e : ClientRequestException) {
            return ActivityResponse(ActivitySerializable(0, null, null, null, null, null), e.message)
        }
    }

    override suspend fun getActivityByID(activityID: Long, token: String): ActivityResponse {
        log.d { "GET Activity by ID" }
        try {
            return client.get {
                if (token.isNotEmpty()) {
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                url("api/activities/$activityID")
            }.body()
        } catch (e : ClientRequestException) {
            return ActivityResponse(ActivitySerializable(0, null, null, null, null, null), e.message)
        }
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom(HttpRequestUrls.api_emulator.url)
            encodedPath = path
        }
    }
}