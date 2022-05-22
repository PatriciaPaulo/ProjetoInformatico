package com.example.splmobile.android.ui.onboarding.navigation

sealed class Screen (val route: String) {
    object Onboarding : Screen(route = "onboarding_screen")
    object Authentication : Screen(route = "authentication_screen")
}