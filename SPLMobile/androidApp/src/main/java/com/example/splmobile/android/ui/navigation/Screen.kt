package com.example.splmobile.android.ui.navigation

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.splmobile.android.R

sealed class Screen (val route: String) {

    object Onboarding : Screen(route = "onboarding_screen")
    object Authentication : Screen(route = "authentication_screen")
    object Login : Screen(route = "login_screen")
    object Register : Screen(route = "register_screen")
    object RecoverPassword : Screen(route = "recover_password")

    object GarbageSpotInfo : Screen(route = "garbage_spot_info")
    object CreateEvent : Screen(route = "create_event")
    object EventInfo : Screen(route = "event_info")
    object EventEdit : Screen(route = "event_edit")
    object EventList : Screen(route = "event_list")
    object UsersInEventList : Screen(route = "users_in_event_list")
    object GarbageSpotList : Screen(route = "garbage_spot_list")
    object MyEventList : Screen(route = "my_event_list")
    object UserProfile : Screen(route = "user_profile")
    object FriendsList : Screen(route = "friends_list")
    object ChatUser : Screen(route = "chat_user")
    object ChatEvent : Screen(route = "chat_event")
    object ActivityInfo : Screen(route = "activity_info")

    object OngoingActivity : Screen(route = "ongoing_activity")
    object ManageGarbage : Screen(route = "manage_garbage")

    object Camera : Screen(route = "camera")

}

sealed class BottomNavItem(var title:String, var icon : ImageVector, var route:String){
    object Home : BottomNavItem("Home", Icons.Default.Home,"home")
    object Map: BottomNavItem("Mapa", Icons.Default.Map,"map")
    object Profile: BottomNavItem("Perfil", Icons.Default.Person,"profile")
    object Community: BottomNavItem("Comunidade", Icons.Default.Group,"community")
    object Chat: BottomNavItem("Conversa", Icons.Default.Chat,"chat")
}