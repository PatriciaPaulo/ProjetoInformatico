package com.example.splmobile.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.auth.screens.LoginScreen
import com.example.splmobile.android.ui.auth.screens.RecoverPasswordScreen
import com.example.splmobile.android.ui.auth.screens.RegisterScreen
import com.example.splmobile.android.ui.main.screens.*
import com.example.splmobile.android.ui.onboarding.screens.AuthenticationScreen
import com.example.splmobile.android.ui.onboarding.screens.OnboardingScreen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel

import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
    log: Logger,
    mainViewModel: MainViewModel,
    authViewModel:AuthViewModel,
    localLixoViewModel: LocalLixoViewModel,
    sharedViewModel: SharedViewModel
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(route = Screen.Authentication.route) {
            AuthenticationScreen(navController = navController,
                authViewModel = authViewModel)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController,
                authViewModel = authViewModel)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screen.RecoverPassword.route) {
            RecoverPasswordScreen()
        }

        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(navController = navController)
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(navController,
                mainViewModel,
                localLixoViewModel,
                authViewModel,
                sharedViewModel,
                log)
        }
        composable(BottomNavItem.Community.screen_route) {
            CommunityScreen(navController = navController)
        }
        composable(BottomNavItem.Chat.screen_route) {
            ChatScreen(navController = navController)
        }
        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen(navController = navController)
        }
    }
}