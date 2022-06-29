package com.example.splmobile.android.ui.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.models.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavHostController,authViewModel: AuthViewModel) {
    var showRequestState by remember {
        mutableStateOf(false)
    }
    var showErrorState by remember {
        mutableStateOf("")
    }

    val loginUIState by authViewModel.loginUIState.collectAsState()
    LaunchedEffect(Unit) {
        authViewModel.loginUIState.collect { loginUIState ->

            when(loginUIState) {
                is AuthViewModel.LoginUIState.Loading -> showRequestState = true
                is AuthViewModel.LoginUIState.Success -> {
                    showRequestState = false
                    navController.navigate(BottomNavItem.Home.screen_route)

                }
                is AuthViewModel.LoginUIState.Error -> {
                    showErrorState = loginUIState.message
                    showRequestState = false
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp), contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .size(100.dp)
                .testTag("circle"),
            shape = CircleShape,
            elevation = 2.dp,

            ) {
            Image(
                painterResource(R.drawable.ic_onboarding_clean),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Sign up here"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {

            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Color.LightGray
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 150.dp, bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val username = remember { mutableStateOf(TextFieldValue()) }
            val password = remember { mutableStateOf(TextFieldValue()) }
            Text(
                text = "Login",
                style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Default)
            )

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Username") },
                value = username.value,
                onValueChange = { username.value = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Password") },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password.value = it })

            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        authViewModel.login(username.value.text, password.value.text)
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (showRequestState) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = "Login")
                    }

                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            ClickableText(
                text = AnnotatedString("Forgot password?"),
                onClick = { },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default
                )
            )
            Text(text = showErrorState)
        }
    }

}


