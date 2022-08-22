import co.touchlab.kermit.Logger
import com.example.splmobile.API_PATH
import com.example.splmobile.dtos.activities.ActivitiesResponse
import com.example.splmobile.dtos.events.EventsResponse
import com.example.splmobile.dtos.events.UserInEventResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UserResponse
import com.example.splmobile.dtos.myInfo.UserSerializable
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

class UserInfoServiceImpl(private val log: Logger, engine: HttpClientEngine) : UserInfoService {
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
                    log.v { message + "API IMP MESSAGE"}
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
    override suspend fun getUser(
        token: String
    ): UserResponse {
        log.d { "Fetching my garbage spots from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }


                url("api/users/me")
            }.body() as UserResponse
        }catch (ex :Exception){
            return UserResponse(UserSerializable(0,"","",""),"$ex")
        }

    }

    override suspend fun getMyGarbageSpots(
        token: String
    ): GarbageSpotsResponse {
        log.d { "Fetching my garbage spots from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/garbageSpots/mine")
            }.body() as GarbageSpotsResponse
        }catch (ex :Exception){
            return GarbageSpotsResponse(emptyList(),"$ex")
        }

    }

    override suspend fun getMyActivities(token: String): ActivitiesResponse {
        log.d { "Fetching my activities from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/activities")
            }.body() as ActivitiesResponse
        }catch (ex :Exception){
            return ActivitiesResponse(emptyList(),"$ex")
        }
    }

    override suspend fun getMyEvents(
        token: String
    ): UserInEventResponse {
        log.d { "Fetching my activities from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/events/mine")
            }.body() as UserInEventResponse
        }catch (ex :Exception){
            return UserInEventResponse(emptyList(),"$ex")
        }
    }

    override suspend fun checkMyEmail(emailRequest: EmailRequest): EmailCheckResponse {
        TODO("Not yet implemented")
    }

    override suspend fun putUser(
        utilizador: UserSerializable,
        token: String
    ): UserResponse {
        log.d { "update logged in user" }
        try{
            return client.put {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(utilizador)
                url("api/users/me")
            }.body() as UserResponse
        }
        catch (ex :Exception){
            return UserResponse(UserSerializable(0,"","",""),"$ex")
        }

    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom(API_PATH)
            encodedPath = path
        }
    }
}