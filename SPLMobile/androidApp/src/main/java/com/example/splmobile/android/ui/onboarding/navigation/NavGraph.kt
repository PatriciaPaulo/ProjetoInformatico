package com.example.splmobile.android.ui.onboarding.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.main.components.BottomNavItem
import com.example.splmobile.android.ui.main.screens.*
import com.example.splmobile.android.ui.main.screens.LocalLixo.LocalLixoScreen
import com.example.splmobile.android.ui.onboarding.screens.AuthenticationScreen
import com.example.splmobile.android.ui.onboarding.screens.OnboardingScreen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.lixeiras.LixeiraViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String, log: Logger, mainViewModel: MainViewModel, lixeiraViewModel: LixeiraViewModel, sharedViewModel: SharedViewModel
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(route = Screen.Authentication.route) {
            AuthenticationScreen()
        }
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen()
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(navController, mainViewModel,lixeiraViewModel,sharedViewModel,log)
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
        composable(com.example.splmobile.android.ui.main.navigation.Screen.LocalLixo.route+ "/{lixeiraID}", arguments = listOf(
            navArgument(name = "lixeiraID") {
            type = NavType.LongType
            defaultValue = -1
            nullable = false
        })) { entity ->
            LocalLixoScreen(navController = navController, lixeiraID = entity.arguments?.getLong("lixeiraID") ?: -1, lixeiraViewModel = lixeiraViewModel)
        }
    }
}