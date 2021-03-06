import com.example.splmobile.dtos.activities.ActivitiesResponse
import com.example.splmobile.dtos.events.EventsResponse
import com.example.splmobile.dtos.events.UserInEventResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.myInfo.EmailCheckResponse
import com.example.splmobile.dtos.myInfo.EmailRequest
import com.example.splmobile.dtos.myInfo.UserResponse
import com.example.splmobile.dtos.myInfo.UserSerializable

interface UserInfoService {
    suspend fun getUser(token: String): UserResponse
    suspend fun getMyGarbageSpots(token: String): GarbageSpotsResponse
    suspend fun getMyActivities(token: String): ActivitiesResponse
    suspend fun getMyEvents(token: String): UserInEventResponse
    suspend fun checkMyEmail(emailRequest: EmailRequest) : EmailCheckResponse
    suspend fun putUser(utilizador: UserSerializable, token: String): UserResponse
}