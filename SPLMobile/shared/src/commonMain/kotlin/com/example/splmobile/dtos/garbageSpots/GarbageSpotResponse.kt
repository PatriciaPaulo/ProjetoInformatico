import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import kotlinx.serialization.Serializable

@Serializable
data class GarbageSpotResponse(
    val data: GarbageSpotDTO,
    val message: String,
)