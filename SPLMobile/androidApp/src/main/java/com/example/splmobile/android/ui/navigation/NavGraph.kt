package com.example.splmobile.android.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.auth.screens.*
import com.example.splmobile.android.ui.main.screens.*
import com.example.splmobile.android.ui.main.screens.events.CreateEventScreen
import com.example.splmobile.android.ui.main.screens.events.EventInfoScreen
import com.example.splmobile.android.ui.main.screens.events.EventListScreen
import com.example.splmobile.android.ui.main.screens.events.MyEventListScreen
import com.example.splmobile.android.ui.main.screens.garbageSpots.GarbageSpotInfoScreen
import com.example.splmobile.android.ui.main.screens.garbageSpots.GarbageSpotsListScreen
import com.example.splmobile.android.ui.main.screens.users.FriendsListScreen
import com.example.splmobile.android.ui.main.screens.users.UserProfileScreen
import com.example.splmobile.android.ui.main.screens.users.UsersInEventListScreen
import com.example.splmobile.android.ui.onboarding.screens.AuthenticationScreen
import com.example.splmobile.android.ui.onboarding.screens.OnboardingScreen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.android.ui.main.screens.activities.OngoingActivity
import com.example.splmobile.android.ui.main.screens.events.*
import com.example.splmobile.android.ui.main.screens.users.*
import com.example.splmobile.android.viewmodel.*
import com.example.splmobile.models.*

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
    activityViewModel: ActivityViewModel,
    mapViewModel: MapViewModel,
    userViewModel: UserViewModel,
    friendViewModel: FriendViewModel,
    messageViewModel: MessageViewModel,
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
                authViewModel = authViewModel,
                userInfoViewModel =userInfoViewModel,
                messageViewModel = messageViewModel)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                messageViewModel = messageViewModel)
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
                mapViewModel = mapViewModel,
                activityViewModel = activityViewModel
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
            ChatScreen(navController = navController,
                messageViewModel = messageViewModel,
                friendViewModel = friendViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                eventViewModel = eventViewModel)
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
        composable(Screen.EventInfo.route+"/{eventId}") { backStackEntry ->
            EventInfoScreen(navController = navController,
                eventViewModel = eventViewModel,
                garbageSpotViewModel = garbageSpotViewModel,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                backStackEntry.arguments?.getString("eventId"),
                log = log)

        }
        composable(Screen.EventEdit.route+"/{eventId}") { backStackEntry ->
            EventEditScreen(navController = navController,
                eventViewModel = eventViewModel,
                garbageSpotViewModel = garbageSpotViewModel,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                backStackEntry.arguments?.getString("eventId"),
                log = log)

        }
        composable(Screen.GarbageSpotInfo.route+"/{garbageSpotId}") { backStackEntry ->
            GarbageSpotInfoScreen(navController = navController,
                garbageSpotViewModel = garbageSpotViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                backStackEntry.arguments?.getString("garbageSpotId"),
                log = log)

        }
        composable(Screen.EventList.route) {
            EventListScreen(navController = navController,
                eventViewModel = eventViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                mainViewModel = mainViewModel,
                log = log)

        }
        composable(Screen.MyEventList.route) {
            MyEventListScreen(navController = navController,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                mainViewModel = mainViewModel,
                log = log)

        }
        composable(Screen.GarbageSpotList.route) {
            GarbageSpotsListScreen(navController = navController,
                garbageSpotViewModel = garbageSpotViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                mainViewModel = mainViewModel,
                log = log)

        }
        composable(Screen.UsersInEventList.route+"/{eventID}") {backStackEntry ->
            UsersInEventListScreen(navController = navController,
                eventViewModel = eventViewModel,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                userInfoViewModel = userInfoViewModel,
                mainViewModel = mainViewModel,
                backStackEntry.arguments?.getString("eventID"),
                log = log)

        }
        composable(Screen.UserProfile.route+"/{userID}") {backStackEntry ->
            UserProfileScreen(navController =navController,
                mainViewModel = mainViewModel,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                friendViewModel = friendViewModel,
                backStackEntry.arguments?.getString("userID"),
                log = log)
        }
        composable(Screen.FriendsList.route) {
            FriendsListScreen(navController =navController,
                mainViewModel = mainViewModel,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                friendViewModel = friendViewModel,
                log = log)
        }
        composable(Screen.ChatUser.route+"/{userID}/{friendshipID}") { backStackEntry->
            ChatUserScreen(navController = navController,
                backStackEntry.arguments?.getString("userID"),
                backStackEntry.arguments?.getString("friendshipID"),
                messageViewModel = messageViewModel,
                userInfoViewModel = userInfoViewModel,
                authViewModel = authViewModel,
                userViewModel=userViewModel)
        }
        composable(Screen.ChatEvent.route+"/{eventID}") { backStackEntry->
            ChatEventScreen(navController = navController,
                backStackEntry.arguments?.getString("eventID"),
                messageViewModel = messageViewModel,
                userInfoViewModel = userInfoViewModel,
                authViewModel = authViewModel,
                userViewModel=userViewModel,
                eventViewModel = eventViewModel)
        }
    }
}