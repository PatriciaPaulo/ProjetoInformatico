package com.example.splmobile.services.files

import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.API_PATH
import com.example.splmobile.dtos.activities.ActivityID
import com.example.splmobile.dtos.files.FileResponse
import com.example.splmobile.dtos.files.FileSerializable
import com.example.splmobile.dtos.garbageSpots.GarbageSpotID
import com.example.splmobile.dtos.users.UserID
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger

@OptIn(InternalAPI::class)
class FileServiceImpl (
    private val log : KermitLogger,
    engine: HttpClientEngine
) : FileService {
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
                    log.v { message + "FILES IMPL" }
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

    override suspend fun postActivityUpload(
        activity: ActivityID,
        file: Unit,
        token: String
    ): FileResponse {
        log.d { "Post Activity File Upload" }
        try {
            return client.submitFormWithBinaryData(
                url = "$API_PATH/api/upload/activities/$activity",
                formData = formData {
                    append(
                        key = "file",
                        value = file,
                    )
                }
            ) {
                if(token.isNotEmpty()){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
            }.body() as FileResponse
        } catch (e : Exception) {
            return FileResponse("$e")
        }
    }

    override suspend fun postGarbageSpotUpload(
        garbageSpot: GarbageSpotID,
        fileRequest: FileSerializable
    ): FileResponse {
        log.d { "Post Garbage Spot File Upload" }
        TODO("Not yet implemented")
    }

    override suspend fun postProfileUpload(
        user: UserID,
        fileRequest: FileSerializable,
        token: String
    ): FileResponse {
        log.d { "Post Profile File Upload" }
        TODO("Not yet implemented")
    }
}