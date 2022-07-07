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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.isTextFieldEmpty

@Composable
fun RecoverPasswordScreen(){
    Column (
        modifier = Modifier
            .padding(dimensionResource(R.dimen.default_margin), dimensionResource(R.dimen.small_spacer))
            .padding(bottom = dimensionResource(R.dimen.small_spacer))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
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
                painterResource(R.drawable.ic_onboarding_clean), //TODO Change icon
                contentDescription = "App Icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.big_spacer)))

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf(false) } // Error flag
        val emailUpdate = { data : String ->
            email = data
            if(!isTextFieldEmpty(email)) {
                emailError = false
            }
        }

        // Title
        Text(
            text = textResource(R.string.recoverPassword).toString(),
            fontSize = dimensionResource(R.dimen.title).value.sp
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium_spacer)))

        // Email box
        // email
        OutlinedTextField(
            value = email,
            onValueChange =  emailUpdate,
            leadingIcon = {
                Image(
                    painterResource(R.drawable.ic_main_home), // TODO Change ICON
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = textResource(R.string.insertEmail).toString(),
                    fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                )},
            placeholder = {
                Text(
                    text = textResource(R.string.emailPlaceholder).toString(),
                    fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                ) },
            singleLine = true,
            isError = emailError,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.btn_large))
        )

        if(emailError) {
            Text(
                text = stringResource(R.string.registerInstead), //TODO Change ERROR
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
            )
        }

        // Send Email Button
        Button(
            onClick = {
                // TODO Send Recover Password Request
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(dimensionResource(R.dimen.btn_small))

        ) {
            /*if(showRequestState) {
                CircularProgressIndicator()
            } else {
                Text(
                    textResource(R.string.lblLogin).toString(),
                    fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                )
            }*/
        }
    }
}

@Composable
fun emailExists(email : String) : Pair<Boolean, Int> {

    return Pair(true, 0)
}