package com.example.splmobile.android.ui.onboarding.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import com.example.splmobile.android.*
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.models.AuthViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking


//Build AuthenticationScreen with buttons to Login, Register and Login as Guest
@Composable
fun AuthenticationScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    val loginUIState by authViewModel.loginUIState.collectAsState()
    LaunchedEffect(Unit) {
        authViewModel.loginUIState.collect { loginUIState ->
            when (loginUIState) {
                is AuthViewModel.LoginUIState.Success -> {
                    navController.navigate(BottomNavItem.Home.screen_route)
                }
                is AuthViewModel.LoginUIState.Error -> {
                    context.dataStore.edit {
                        it.clear()
                    }
                }
            }
        }
    }

    val authResult = isAuthenticated()

    // if user already authenticated try to login
    if(authResult.first){
        // Attempt Login
        authViewModel.login(authResult.second, authResult.third)
    }

    // if login failed build Authentication Screen
    AuthenticationUI(navController)
}

// Check if user was previously logged in
@Composable
fun isAuthenticated() : Triple<Boolean, String, String> {
    val context = LocalContext.current

    val checkDataStore = runBlocking {
        context.dataStore.data.first()
    }

    if (checkDataStore.asMap().isEmpty()) {
        return Triple(false, "", "")
    }

    return Triple(true, checkDataStore[stringPreferencesKey(EMAIL_KEY)].orEmpty(), checkDataStore[stringPreferencesKey(PASSWORD_KEY)].orEmpty())
}

// Build Authentication Screen
@Composable
fun AuthenticationUI (
    navController : NavHostController
){
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

