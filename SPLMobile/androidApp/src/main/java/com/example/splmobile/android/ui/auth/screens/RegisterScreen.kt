package com.example.splmobile.android.ui.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.isEmailValid
import com.example.splmobile.isTextFieldEmpty
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.passwordsMatch

@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    /* TODO
        Mudar os ICONS dos text boxes para um user e uma key
        Mudar ICON do App Icon
     */

    // Register Layout
    Column (
        modifier = Modifier
            .padding(top = dimensionResource(R.dimen.default_margin))
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Card(
            modifier = Modifier
                .size(dimensionResource(R.dimen.small_logo))
                .testTag("circle"),
            shape = CircleShape,
            elevation = 2.dp,
        ) {
            Image(
                painterResource(R.drawable.ic_onboarding_clean),
                contentDescription = "App Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.big_spacer)))

        // TextFields updating according to state
        RegisterComposableUI(navController, authViewModel)
    }
}

@Composable
fun RegisterComposableUI(
    navController: NavHostController,
    authViewModel: AuthViewModel
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

    var passwordConfirmation by remember { mutableStateOf("") }
    var passwordConfError by remember { mutableStateOf(false) } // Error flag
    val passwordConfUpdate = { data : String ->
        passwordConfirmation = data
        if(!isTextFieldEmpty(passwordConfirmation)) {
            passwordConfError = false
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
    val registerUIState by authViewModel.registerUIState.collectAsState()
    LaunchedEffect(Unit) {
        authViewModel.registerUIState.collect { registerUIState ->

            when (registerUIState) {
                is AuthViewModel.RegisterUIState.Loading -> showRequestState = true
                is AuthViewModel.RegisterUIState.Success -> {
                    context.dataStore.edit { settings ->
                        settings[stringPreferencesKey(EMAIL_KEY)] = email
                        settings[stringPreferencesKey(PASSWORD_KEY)] = password
                    }

                    showRequestState = false
                    navController.navigate(Screen.Login.route)
                }
                is AuthViewModel.RegisterUIState.Error -> {
                    showErrorState = registerUIState.message
                    showRequestState = false
                }
            }
        }
    }

    RegisterValidationUI(
        email = email,
        emailError = emailError,
        emailUpdate = emailUpdate,
        password = password,
        passwordError = passwordError,
        passwordUpdate = passwordUpdate,
        passwordConf = passwordConfirmation,
        passwordConfError = passwordConfError,
        passwordConfUpdate = passwordConfUpdate,
        showRequestState = showRequestState,
        showErrorState = showErrorState,
        navController = navController
    ) {
        // validate()
        if(isEmailValid(email) && !isTextFieldEmpty(password) && !isTextFieldEmpty(passwordConfirmation) && passwordsMatch(password, passwordConfirmation)) {
            authViewModel.registerUser(email, password, passwordConfirmation)
        } else {
            emailError = !isEmailValid(email)
            passwordError = isTextFieldEmpty(password)
            passwordConfError = isTextFieldEmpty(passwordConfirmation) && passwordsMatch(password, passwordConfirmation)
        }
    }
}


// TODO Change ERROR text
// TODO Change ALL text (has logins text)
@Composable
fun RegisterValidationUI(
    email : String,
    emailError : Boolean,
    emailUpdate : (String) -> Unit,
    password : String,
    passwordError : Boolean,
    passwordUpdate : (String) -> Unit,
    passwordConf : String,
    passwordConfError : Boolean,
    passwordConfUpdate : (String) -> Unit,
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

        // Register by Email
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
                        painterResource(R.drawable.ic_main_home),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.insertEmail),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                },
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
                    Image(
                        painterResource(R.drawable.ic_main_home),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.insertPassword),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.passwordPlaceholder),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                },
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

            // password confirmation
            OutlinedTextField(
                value = passwordConf,
                onValueChange = passwordConfUpdate,
                leadingIcon = {
                    Image(
                        painterResource(R.drawable.ic_main_home),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.insertPassword),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.passwordPlaceholder),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                },
                singleLine = true,
                isError = passwordConfError,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.btn_large))
            )
            if(passwordConfError) {
                //TODO Tratar dos erros
                Text(
                    text = stringResource(R.string.registerInstead),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }

            if(showErrorState.isNotBlank()) {
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
                    text = AnnotatedString(textResource(R.string.forgotPassword).toString()),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp
                )
            }

            // Register Button
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

            // Line Break
            /*
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Divider(
                    color = Color.Blue,
                    thickness = dimensionResource(R.dimen.hr),
                    modifier = Modifier
                        .weight(4f)
                )
                Text(
                    textResource(R.string.orLineBreak).toString(),
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.Center
                )
                Divider(
                    color = Color.Blue,
                    thickness = dimensionResource(R.dimen.hr),
                    modifier = Modifier
                        .weight(4f)
                )
            }
            */
        }

        // Login Instead
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ){
            TextButton(
                onClick = {
                    navController.navigate(Screen.Login.route)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = AnnotatedString(
                        text = textResource(R.string.noAccountYet),
                        spanStyle = SpanStyle(color = MaterialTheme.colors.onBackground)
                    ).plus(
                        AnnotatedString(
                            text = " " + textResource(R.string.registerInstead),
                        )
                    ),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp,
                )
            }
        }
    }
}