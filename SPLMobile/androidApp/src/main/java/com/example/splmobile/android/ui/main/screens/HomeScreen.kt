package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.activities.CreateActivitySerializable
import com.example.splmobile.viewmodels.ActivityViewModel
import com.example.splmobile.viewmodels.AuthViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController : NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel,
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            HomeScreenUI(navController, authViewModel, activityViewModel, mapViewModel)
        }
    )
}

@Composable
fun HomeScreenUI(
    navController: NavController,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel,
    mapViewModel: MapViewModel
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.default_margin))
    ){
        println("HOME")
        Row(){
            // Button Start Activity
            Button(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.btn_large))
                    .fillMaxWidth(),
                onClick = {
                    startNewActivity(navController, activityViewModel, authViewModel, mapViewModel)
                }
            ){
                Text(
                    text = "Começar Atividade" //TODO Change to string res
                )
            }

            // Button Create GarbageSpot
        }

        // Button Take Picture
        Button(
            modifier = Modifier
                .height(dimensionResource(R.dimen.btn_large))
                .fillMaxWidth(),
            onClick = {
                navController.navigate(Screen.Camera.route)
            }
        ){
            Text(
                text = "Tirar Foto amor"
            )
        }
    }

}

fun startNewActivity(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel
){

    // Create Activity in DB
    activityViewModel.createActivity(
        CreateActivitySerializable(null),
        authViewModel.tokenState.value,
    )

    // Open New Activity Page
    navController.navigate(Screen.OngoingActivity.route)

}
