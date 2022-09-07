package com.example.splmobile.android.ui.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import com.example.splmobile.android.*
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.isEmailValid
import com.example.splmobile.isTextFieldEmpty
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.MessageViewModel
import com.example.splmobile.models.UserInfoViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    messageViewModel: MessageViewModel
) {

    // Login Layout
    Column (
        modifier = Modifier
            .padding(top = dimensionResource(R.dimen.default_margin))
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //App Icon
        Image(
            painterResource(R.drawable.ic_logo),
            contentDescription = "App Icon",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(dimensionResource(R.dimen.big_logo))
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.big_spacer)))

        // TextFields updating according to state
        LoginComposableUI(navController, authViewModel, userInfoViewModel,messageViewModel)

    }


}

@Composable
fun LoginComposableUI(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    messageViewModel: MessageViewModel
){
    // Variables for TextFields Validation
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) } // Error flag
    val emailUpdate = { data : String ->
        email = data
        if(!isTextFieldEmpty(email)) {
            emailError = false
        }
    }

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) } // Error flag
    val passwordUpdate = { data : String ->
        password = data
        if(!isTextFieldEmpty(password)) {
            passwordError = false
        }
    }

    // Variables for API Request Validation
    var showRequestState by remember {
        mutableStateOf(false)
    }
    var showErrorState by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val loginUIState by authViewModel.loginUIState.collectAsState()
    LaunchedEffect(Unit) {
        authViewModel.loginUIState.collect { loginUIState ->

            when (loginUIState) {
                is AuthViewModel.LoginUIState.Loading -> showRequestState = true
                is AuthViewModel.LoginUIState.Success -> {

                        context.dataStore.edit { settings ->
                            settings[stringPreferencesKey(EMAIL_KEY)] = email
                            settings[stringPreferencesKey(PASSWORD_KEY)] = password

                        //connect to websocket
                        messageViewModel.startConnection(authViewModel.tokenState.value)
                        userInfoViewModel.getMyInfo(authViewModel.tokenState.value)

                        showRequestState = false
                        navController.navigate(BottomNavItem.Home.route)
                    }

                }
                is AuthViewModel.LoginUIState.Error -> {
                    showErrorState = loginUIState.message
                    showRequestState = false
                }
            }
        }
    }

    LoginValidationUI(
        email = email,
        emailError = emailError,
        emailUpdate = emailUpdate,
        password = password,
        passwordError = passwordError,
        passwordUpdate = passwordUpdate,
        showRequestState = showRequestState,
        showErrorState = showErrorState,
        navController = navController
    ) {
        // validate()
        if(isEmailValid(email) && !isTextFieldEmpty(password)){
            authViewModel.login(email, password)
        } else {
            emailError = !isEmailValid(email)
            passwordError = isTextFieldEmpty(password)
        }
    }
}


// TODO Change ERROR text
@Composable
fun LoginValidationUI(
    email : String,
    emailError : Boolean,
    emailUpdate : (String) -> Unit,
    password : String,
    passwordError : Boolean,
    passwordUpdate : (String) -> Unit,
    navController: NavHostController,
    showRequestState : Boolean,
    showErrorState : String,
    validate : () -> Unit
) {
    // Login Layout
    Column(
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.default_margin))
            .fillMaxHeight()
    ) {

        // Login by Email
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = dimensionResource(R.dimen.small_spacer))
                .fillMaxWidth()
                .weight(10f),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(R.string.lblLoginToYourAcc),
                style = TextStyle(fontSize = dimensionResource(R.dimen.small_title).value.sp),
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.small_spacer)))

            // email
            OutlinedTextField(
                value = email,
                onValueChange =  emailUpdate,
                leadingIcon = {
                    Image(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.insertEmail),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )},
                placeholder = {
                    Text(
                        text = stringResource(R.string.emailPlaceholder),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    ) },
                singleLine = true,
                isError = emailError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.btn_large))
            )
            if(emailError) {
                Text(
                    text = stringResource(R.string.registerInstead),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.small_spacer)))

            // password
            OutlinedTextField(
                value = password,
                onValueChange = passwordUpdate,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.insertPassword),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )},
                placeholder = {
                    Text(
                        text = stringResource(R.string.passwordPlaceholder),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )},
                singleLine = true,
                isError = passwordError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.btn_large))
            )
            if(passwordError) {
                Text(
                    text = stringResource(R.string.registerInstead),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }

            if(!showErrorState.isBlank()) {
                Text(
                    text = showErrorState,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // Recover Password
            TextButton(
                onClick = {
                    navController.navigate(Screen.RecoverPassword.route)
                },
                modifier = Modifier
                    .align(Alignment.End),
            ) {
                Text(
                    text = AnnotatedString(stringResource(R.string.forgotPassword)),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp
                )
            }

            // Login Button
            Button(
                onClick = {
                    validate()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(dimensionResource(R.dimen.btn_small))

            ) {
                if(showRequestState) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        stringResource(R.string.lblLogin),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                }
            }
        }

        // Register Instead
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ){
            TextButton(
                onClick = {
                    navController.navigate(Screen.Register.route)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = AnnotatedString(
                        text = stringResource(R.string.noAccountYet),
                        spanStyle = SpanStyle(color = MaterialTheme.colors.onBackground)
                    ).plus(
                        AnnotatedString(
                            text = " " + stringResource(R.string.registerInstead),
                        )
                    ),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp,
                )
            }
        }
    }
}


