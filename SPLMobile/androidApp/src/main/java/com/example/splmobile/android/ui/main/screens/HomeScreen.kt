package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splmobile.android.CameraView
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.activities.ActivitySerializable
import com.example.splmobile.dtos.activities.CreateActivitySerializable
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.google.accompanist.pager.ExperimentalPagerApi


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
/*
    CameraView(onImageCaptured = { uri, fromGallery ->
        Log.d("IMG", "Image Uri Captured from Camera View")
        //Todo : use the uri as needed
    }, onError = { imageCaptureException ->
        Log.d("ERROR IMG","An error occurred while trying to take a picture")
    })
*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.default_margin))
    ){
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
                    text = "Começar Atividade"
                )
            }

            // Button Create GarbageSpot
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
