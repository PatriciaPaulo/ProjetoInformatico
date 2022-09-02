import com.example.splmobile.objects.activities.ActivitiesResponse
import com.example.splmobile.objects.events.UserInEventResponse
import com.example.splmobile.objects.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.objects.myInfo.EmailCheckResponse
import com.example.splmobile.objects.myInfo.EmailRequest
import com.example.splmobile.objects.myInfo.UserResponse
import com.example.splmobile.objects.myInfo.UserSerializable

interface UserInfoService {
    suspend fun getUser(token: String): UserResponse
    suspend fun getMyGarbageSpots(token: String): GarbageSpotsResponse
    suspend fun getMyActivities(token: String): ActivitiesResponse
    suspend fun getMyEvents(token: String): UserInEventResponse
    suspend fun checkMyEmail(emailRequest: EmailRequest) : EmailCheckResponse
    suspend fun putUser(utilizador: UserSerializable, token: String): UserResponse
}