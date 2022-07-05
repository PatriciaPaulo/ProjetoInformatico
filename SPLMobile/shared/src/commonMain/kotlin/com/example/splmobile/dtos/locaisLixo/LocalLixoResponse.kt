import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import kotlinx.serialization.Serializable

@Serializable
data class LocalLixoResponse(
    val data: LocalLixoSer,
    val message: String,
    val status: String,
)