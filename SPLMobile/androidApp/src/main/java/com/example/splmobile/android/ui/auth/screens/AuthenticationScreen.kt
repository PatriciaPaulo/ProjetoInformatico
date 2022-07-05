package com.example.splmobile.android.ui.onboarding.screens

import androidx.annotation.StringRes
import com.example.splmobile.android.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.navigation.Screen


//Build AuthenticationScreen with buttons to Login, Register and Login as Guest
@Composable
fun AuthenticationScreen(
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){
        // TODO Box for Graphics

        // Box for Buttons
        Column(
            modifier = Modifier
                .padding(
                    dimensionResource(R.dimen.default_margin),
                    0.dp,
                    dimensionResource(R.dimen.default_margin),
                    dimensionResource(R.dimen.default_margin)
                )
        ) {
            // Login Button
            btnGoToLogin(Modifier) {
                navController.navigate(Screen.Login.route)
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium_spacer)))

            // Register Button
            btnGoToRegister(Modifier) {
                navController.navigate(Screen.Register.route)
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium_spacer)))

            // Visit as Guest Button
            btnGoToVisitGuest(Modifier) {
                //TODO go to main page as guest
            }
        }
    }
}


// Go to Login
@Composable
fun btnGoToLogin(
    modifier: Modifier,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.btn_small))
    ) {
        Text( textResource(R.string.lblLogin).toString() )
    }
}

// Go to Register
@Composable
fun btnGoToRegister(
    modifier: Modifier,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.btn_small))
    ) {
        Text( textResource(R.string.lblRegister).toString() )
    }
}

// Go to Visit as Guest
@Composable
fun btnGoToVisitGuest(
    modifier: Modifier,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.btn_small))
    ) {
        Text( textResource(R.string.lblLoginGuest).toString() )
    }
}

