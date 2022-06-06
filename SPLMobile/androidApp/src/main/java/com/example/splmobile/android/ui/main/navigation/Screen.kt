package com.example.splmobile.android.ui.main.navigation

sealed class Screen (val route: String) {
    object Map : Screen(route = "map_screen")
    object Profile : Screen(route = "profile_screen")
    object Community : Screen(route = "community_screen")
    object Chat : Screen(route = "chat_screen")
    object Home : Screen(route = "home_screen")
    object LocalLixo : Screen(route = "local_lixo")

    fun withArgs(vararg args: String):String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

