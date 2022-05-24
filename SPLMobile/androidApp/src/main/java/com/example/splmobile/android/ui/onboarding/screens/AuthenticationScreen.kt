package com.example.splmobile.android.ui.onboarding.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// TODO Change Text to buttons of login and register options

@Composable
fun AuthenticationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "HOME" //TODO mudar para @strings
        )
        /*
        btnAuthenticationLogin(
            modifier = Modifier,
        ){
            //Go to Login Page
        }*/
    }
}

@Composable
fun btnAuthenticationLogin (
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 40.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        // To show the button only in page 2
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Login" //TODO Change to @strings/
            )
        }
    }
}