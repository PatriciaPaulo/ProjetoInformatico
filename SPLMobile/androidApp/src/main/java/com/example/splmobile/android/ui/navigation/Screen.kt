package com.example.splmobile.android.ui.navigation

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
    object EventList : Screen(route = "event_list")
    object UsersInEventList : Screen(route = "users_in_event_list")
    object GarbageSpotList : Screen(route = "garbage_spot_list")
    object MyEventList : Screen(route = "my_event_list")
    object UserProfile : Screen(route = "user_profile")
    object FriendsList : Screen(route = "friends_list")

    object OngoingActivity : Screen(route = "ongoing_activity")

}

sealed class BottomNavItem(var title:String, var icon:Int, var route:String){
    object Home : BottomNavItem("Home", R.drawable.ic_main_home,"home")
    object Map: BottomNavItem("Map", R.drawable.ic_main_map,"map")
    object Profile: BottomNavItem("Profile", R.drawable.ic_main_profile,"profile")
    object Community: BottomNavItem("Community", R.drawable.ic_main_community,"community")
    object Chat: BottomNavItem("Chat", R.drawable.ic_main_chat,"chat")
}