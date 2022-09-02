package com.example.splmobile.android.ui.main.screens.garbageSpots


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.viewmodels.AuthViewModel
import com.example.splmobile.viewmodels.GarbageSpotViewModel
import com.example.splmobile.viewmodels.UserViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GarbageSpotInfoScreen(
    navController: NavController,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    garbageSpotId: String?,
    log: Logger
) {
    val log = log.withTag("GarbageSpotInfoScreen")
    LaunchedEffect(Unit) {

        garbageSpotViewModel.emptyUpdateGSState()
        garbageSpotViewModel.getGarbageSpotById(
            garbageSpotId!!.toLong(),
            authViewModel.tokenState.value
        )


    }


    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->


        GarbageSpotInfoUI(
            garbageSpotViewModel,
            userViewModel,
            authViewModel,
            innerPadding,
            garbageSpotId
        )

    }


}

@Composable
private fun GarbageSpotInfoUI(
    garbageSpotViewModel: GarbageSpotViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    innerPadding: PaddingValues,
    garbageSpotId: String?
) {
    when (val garbageSpotByIdState =
        garbageSpotViewModel.garbageSpotUIState.collectAsState().value) {
        is GarbageSpotViewModel.GarbageSpotUIState.GarbageSpotByIdSuccess -> {

            CreatorSection(userViewModel, garbageSpotByIdState, authViewModel)


            GarbageSpotComponent(
                innerPadding,
                garbageSpotId,
                garbageSpotByIdState,
                userViewModel,
                garbageSpotViewModel,
                authViewModel
            )



        }
    }
    ResponseState(garbageSpotViewModel, authViewModel, garbageSpotId)
}

@Composable
private fun CreatorSection(
    userViewModel: UserViewModel,
    garbageSpotByIdState: GarbageSpotViewModel.GarbageSpotUIState.GarbageSpotByIdSuccess,
    authViewModel: AuthViewModel
) {
    userViewModel.getUserStats(
        garbageSpotByIdState.garbageSpot.creator,
        authViewModel.tokenState.value
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GarbageSpotComponent(
    innerPadding: PaddingValues,
    garbageSpotId: String?,
    garbageSpotByIdState: GarbageSpotViewModel.GarbageSpotUIState.GarbageSpotByIdSuccess,
    userViewModel: UserViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel
) {

    val statusListEvent = listOf(
        textResource(R.string.GarbageSpotStatusListElement1),
        textResource(R.string.GarbageSpotStatusListElement2),
        textResource(R.string.GarbageSpotStatusListElement3)
    )
    val creatorState = userViewModel.usersUIState.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .background(colorResource(id = R.color.cardview_dark_background))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Local de lixo #${garbageSpotByIdState.garbageSpot.id}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
        Text(
            text = "${garbageSpotByIdState.garbageSpot.name}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        if (garbageSpotByIdState.garbageSpot.approved) {
            Text(
                text = "Aprovado",
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        } else {
            Text(
                text = "Não aprovado",
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
        when (creatorState) {
            is UserViewModel.UsersUIState.SuccessUser -> {
                Text(
                    text = "Criado por ${creatorState.user.username}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
            is UserViewModel.UsersUIState.Error -> {
                Text(
                    text = "Criado por ${garbageSpotByIdState.garbageSpot.creator}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }
        Text(
            text = "${garbageSpotByIdState.garbageSpot.createdDate}",
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(garbageSpotByIdState.garbageSpot.status) }

        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = { },
                    label = { Text(textResource(R.string.lblEventParticipateStatus)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    statusListEvent.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            }
                        ) {
                            Text(text = selectionOption)
                        }
                    }
                }
            }
        }
        val statusState = ButtonState(selectedOptionText, garbageSpotByIdState)
        ButtonSection(
            garbageSpotViewModel,
            garbageSpotId,
            selectedOptionText,
            authViewModel,
            statusState
        )
    }

}

@Composable
private fun ResponseState(
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    garbageSpotId: String?,
    ) {
    when (garbageSpotViewModel.garbageSpotUpdateUIState.collectAsState().value) {
        is GarbageSpotViewModel.GarbageSpotUpdateUIState.Success -> {
            garbageSpotViewModel.getGarbageSpotById(garbageSpotId!!.toLong(),authViewModel.tokenState.value)
            Text(textResource(id = R.string.txtGarbageSpotStatusUpdateSuccess))
        }
    }
}

@Composable
private fun ButtonSection(
    garbageSpotViewModel: GarbageSpotViewModel,
    garbageSpotId: String?,
    selectedOptionText: String,
    authViewModel: AuthViewModel,
    statusState: MutableState<Boolean>
) {
    Button(
        onClick = {
            garbageSpotViewModel.updateGarbageSpotEstado(
                garbageSpotId!!.toLong(),
                selectedOptionText,
                authViewModel.tokenState.value
            )
        },
        enabled = statusState.value,
        modifier = Modifier
            .fillMaxWidth(),

        ) {
        Text(text = textResource(R.string.btnUpdateParticipateOnEvent).toString())
    }
}

@Composable
private fun ButtonState(
    selectedOptionText: String,
    garbageSpotByIdState: GarbageSpotViewModel.GarbageSpotUIState.GarbageSpotByIdSuccess
): MutableState<Boolean> {
    val statusState = remember { mutableStateOf(false) }
    when (selectedOptionText) {
        else -> {
            if (selectedOptionText == garbageSpotByIdState.garbageSpot.status) {
                statusState.value = false
            } else {
                statusState.value = true
            }
        }
    }
    return statusState
}



