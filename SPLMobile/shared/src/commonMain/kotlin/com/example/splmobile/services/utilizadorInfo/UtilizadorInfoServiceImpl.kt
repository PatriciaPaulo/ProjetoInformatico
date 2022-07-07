import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.eventos.EventosResponse
import com.example.splmobile.dtos.locaisLixo.LocaisLixoResponse
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UtilizadorResponse
import com.example.splmobile.dtos.myInfo.UtilizadorSer
import com.example.splmobile.services.locaisLixo.LocalLixoService
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
import kotlin.collections.get

class UtilizadorInfoServiceImpl(private val log: Logger, engine: HttpClientEngine) : UtilizadorInfoService {
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
    override suspend fun getUtilizador(
        token: String
    ): UtilizadorResponse {
        log.d { "Fetching my locais lixo from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)

                url("api/users/me")
            }.body() as UtilizadorResponse
        }catch (ex :HttpRequestTimeoutException){
            return UtilizadorResponse(UtilizadorSer(0,"","",""),"error","500")
        }
        return UtilizadorResponse(UtilizadorSer(0,"","",""),"error","400")
    }

    override suspend fun getMyLocaisLixo(
        token: String
    ): LocaisLixoResponse {
        log.d { "Fetching my locais lixo from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/lixeiras/mine")
            }.body() as LocaisLixoResponse
        }catch (ex :HttpRequestTimeoutException){
            return LocaisLixoResponse(emptyList(),"error","500")
        }
        return LocaisLixoResponse(emptyList(),"error","400")
    }

    override suspend fun getMyEventos(
        token: String
    ): EventosResponse {
        TODO("Not yet implemented")
    }

    override suspend fun checkMyEmail(emailRequest: EmailRequest): EmailCheckResponse {
        TODO("Not yet implemented")
    }

    override suspend fun putUtilizador(
        utilizador: UtilizadorSer,
        token: String
    ): UtilizadorResponse {
        log.d { "update Local Lixo" }
        try{
            return client.put {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(utilizador)
                url("api/users/me")
            }.body() as UtilizadorResponse
        }
        catch (ex :HttpRequestTimeoutException){
            return UtilizadorResponse(UtilizadorSer(0,"","",""),"error","500")
        }
        return UtilizadorResponse(UtilizadorSer(0,"","",""),"error","400")
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }
}