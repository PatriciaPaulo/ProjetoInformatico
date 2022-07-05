package com.example.splmobile.android.ui.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource

@Composable
fun RecoverPasswordScreen(){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.default_margin)),
        verticalArrangement = Arrangement.Center
    ){
        val email = remember { mutableStateOf(TextFieldValue()) }

        // Title
        Text(
            text = textResource(R.string.recoverPassword).toString(),
            fontSize = dimensionResource(R.dimen.title).value.sp
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium_spacer)))

        // Email box
        // email
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            leadingIcon = {
                Image(
                    painterResource(R.drawable.ic_main_home),
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
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.btn_large))
        )

        // Button
    }
}