package com.example.splmobile.android.ui.main.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.main.components.BottomNavItem
import com.example.splmobile.android.ui.main.screens.*
import com.example.splmobile.models.LixeiraViewModel

import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun Navigation(navController: NavHostController, log: Logger, lixeiraViewModel: LixeiraViewModel){

    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen()
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(lixeiraViewModel,log)
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