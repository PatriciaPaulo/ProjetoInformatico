import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.eventos.EventosResponse
import com.example.splmobile.dtos.locaisLixo.LocaisLixoResponse
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UtilizadorResponse

interface UtilizadorInfoService {
    suspend fun getUtilizador(token: String): UtilizadorResponse
    suspend fun getMyLocaisLixo(token: String): LocaisLixoResponse
    suspend fun getMyEventos(token: String): EventosResponse
    suspend fun checkMyEmail(emailRequest: EmailRequest) : EmailCheckResponse
}