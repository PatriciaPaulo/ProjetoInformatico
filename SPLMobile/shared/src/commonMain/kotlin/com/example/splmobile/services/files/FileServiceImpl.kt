package com.example.splmobile.services.files

import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.API_PATH
import com.example.splmobile.HttpRequestUrls
import com.example.splmobile.objects.activities.ActivityID
import com.example.splmobile.objects.files.FileResponse
import com.example.splmobile.objects.files.FileSerializable
import com.example.splmobile.objects.garbageSpots.GarbageSpotID
import com.example.splmobile.objects.users.UserID
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger


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
                    log.v { message + " FILES IMPL" }
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
        token: String,
        uploadFiles: Map<String, ByteArray>,
    ): FileResponse {
        log.d { "Post Activity File Upload" }
        /*
        val createFile : VfsFile = VfsFileFromData(path)
        val file = createFile.readBytes()
        var filename_op = path.split("/")
        var filename = filename_op.last()

        println(createFile.isFile())
        println(createFile)
        println(filename)

         */

        try {
            val parts: List<PartData> = formData {
                for (file in uploadFiles) {
                    this.append(
                        file.key,
                        file.value,
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                        }
                    )
                    println(file.key)
                    println(file.value)
                    println(file.value.size)
                }
            }

            return client.submitFormWithBinaryData (
                formData = formData{
                    for (file in uploadFiles) {
                        append("name", file.key)
                        append("file", file.value, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"${file.key}\"")
                        })
                    }
                }
            ) {
                url("/api/upload/activities/${activity.id}")
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

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom(HttpRequestUrls.api_emulator.url)
            encodedPath = path
        }
    }
}