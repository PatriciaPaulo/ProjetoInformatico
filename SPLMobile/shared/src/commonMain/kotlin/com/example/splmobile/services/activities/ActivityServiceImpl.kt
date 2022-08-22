package com.example.splmobile.services.activities


import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.API_PATH
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.activities.ActivitiesTypeResponse
import com.example.splmobile.dtos.activities.ActivitySerializable
import com.example.splmobile.dtos.activities.CreateActivitySerializable
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
                    log.v { message + "ACTIVITY IMP MESSAGE" }
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
    ) : RequestMessageResponse  {
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

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom(API_PATH)
            encodedPath = path
        }
    }
}