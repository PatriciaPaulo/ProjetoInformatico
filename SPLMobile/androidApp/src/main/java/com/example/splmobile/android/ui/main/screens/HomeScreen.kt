package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.dtos.activities.CreateActivitySerializable
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController : NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log : Logger
) {
    val log = log.withTag("HomeScreen")

    var createActivityState = activityViewModel.activityCreateUIState.collectAsState().value
    when (createActivityState) {
        is ActivityViewModel.ActivityStartUIState.Success -> {
            log.d { "Create New Activity Successful" }
            activityViewModel.setCurrentActivity(createActivityState.currentActivity)

            navController.navigate(Screen.OngoingActivity.route)
        }

        is ActivityViewModel.ActivityStartUIState.Error -> {
            log.d { "Create New Activity Failed" }
            // TODO Show error
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            HomeScreenUI(navController, activityViewModel, authViewModel, log)
        }
    )
}

@Composable
fun HomeScreenUI(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log: Logger
){
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
                    log.d { "Create New Activity" }

                    // Create Activity in DB
                    activityViewModel.createActivity(
                        CreateActivitySerializable(null),
                        authViewModel.tokenState.value,
                    )
                }
            ){
                Text(
                    text = "Começar Atividade" //TODO Change to string res
                )
            }

        }
    }
}
