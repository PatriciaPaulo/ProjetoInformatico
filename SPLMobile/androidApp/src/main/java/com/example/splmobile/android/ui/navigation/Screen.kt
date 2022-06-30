package com.example.splmobile.android.ui.navigation

import com.example.splmobile.android.R

sealed class Screen (val route: String) {

    object Onboarding : Screen(route = "onboarding_screen")
    object Authentication : Screen(route = "authentication_screen")

    //
    object Login : Screen(route = "login_screen")
    //

    object LocalLixo : Screen(route = "local_lixo")
    object CreateLocalLixo : Screen(route = "create_local_lixo")

}

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){
    object Home : BottomNavItem("Home", R.drawable.ic_main_home,"home")
    object Map: BottomNavItem("Map", R.drawable.ic_main_map,"map")
    object Profile: BottomNavItem("Profile", R.drawable.ic_main_profile,"profile")
    object Community: BottomNavItem("Community", R.drawable.ic_main_community,"community")
    object Chat: BottomNavItem("Chat", R.drawable.ic_main_chat,"chat")
}