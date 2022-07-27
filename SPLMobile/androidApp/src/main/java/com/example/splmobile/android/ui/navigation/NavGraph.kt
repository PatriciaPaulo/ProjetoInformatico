package com.example.splmobile.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.auth.screens.LoginScreen
import com.example.splmobile.android.ui.auth.screens.RecoverPasswordScreen
import com.example.splmobile.android.ui.auth.screens.RegisterScreen
import com.example.splmobile.android.ui.main.screens.*
import com.example.splmobile.android.ui.main.screens.activities.OngoingActivity
import com.example.splmobile.android.ui.main.screens.events.CreateEventScreen
import com.example.splmobile.android.ui.onboarding.screens.AuthenticationScreen
import com.example.splmobile.android.ui.onboarding.screens.OnboardingScreen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel

import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel
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
    garbageSpotViewModel: GarbageSpotViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventViewModel: EventViewModel,
    sharedViewModel: SharedViewModel,
    activityViewModel: ActivityViewModel,
    mapViewModel: MapViewModel,
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
                authViewModel = authViewModel,
                userInfoViewModel =userInfoViewModel)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController,
                authViewModel = authViewModel,
                userInfoViewModel =userInfoViewModel)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }
        composable(route = Screen.RecoverPassword.route) {
            RecoverPasswordScreen()
        }

        composable(BottomNavItem.Home.route) {
            HomeScreen(navController = navController,
                authViewModel = authViewModel,
                activityViewModel = activityViewModel,
                mapViewModel = mapViewModel,
            )
        }

        composable(route = Screen.OngoingActivity.route) {
            OngoingActivity(
                navController = navController,
                mapViewModel = mapViewModel
            )
        }
        composable(BottomNavItem.Map.route) {
            MapScreen(navController =navController,
                mainViewModel = mainViewModel,
                garbageSpotViewModel = garbageSpotViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                sharedViewModel = sharedViewModel,
                log = log)
        }
        composable(BottomNavItem.Community.route) {
            CommunityScreen(navController = navController,
                mainViewModel = mainViewModel,
                garbageSpotViewModel = garbageSpotViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                eventViewModel = eventViewModel,
                sharedViewModel = sharedViewModel,
                log = log)
        }
        composable(BottomNavItem.Chat.route) {
            ChatScreen(navController = navController)
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(navController =navController,
                mainViewModel = mainViewModel,
                userInfoViewModel = userInfoViewModel,
                authViewModel = authViewModel,
                sharedViewModel = sharedViewModel,
                log = log)
        }
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(navController = navController,
                mainViewModel = mainViewModel,
                garbageSpotViewModel = garbageSpotViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                eventViewModel = eventViewModel,
                sharedViewModel = sharedViewModel,
                log = log)
        }
    }
}