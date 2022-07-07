import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import kotlinx.serialization.Serializable

@Serializable
data class GarbageSpotResponse(
    val data: GarbageSpotSerializable,
    val message: String,
    val status: String,
)