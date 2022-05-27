package com.example.splmobile.android.ui.main.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splmobile.android.ui.main.components.BottomNavItem
import com.example.splmobile.android.ui.main.screens.*

import com.example.splmobile.android.ui.onboarding.screens.AuthenticationScreen
import com.example.splmobile.android.ui.onboarding.screens.OnboardingScreen
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun Navigation(navController: NavHostController){
    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen()
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen()
        }
        composable(BottomNavItem.Community.screen_route) {
            CommunityScreen()
        }
        composable(BottomNavItem.Chat.screen_route) {
            ChatScreen()
        }
        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen()
        }
    }
}