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
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.models.UserInfoViewModel


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GarbageSpotInfoScreen(
    navController: NavController,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    garbageSpotId: String?,
    log: Logger
) {
    val log = log.withTag("GarbageSpotInfoScreen")
    LaunchedEffect(Unit) {

        //garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
        garbageSpotViewModel.getGarbageSpotById(garbageSpotId!!.toLong(),authViewModel.tokenState.value)


    }
    var garbageSpotByIdState = garbageSpotViewModel.garbageSpotsUIState.collectAsState().value


    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        when (garbageSpotByIdState) {
            is GarbageSpotViewModel.GarbageSpotsUIState.GarbageSpotByIdSuccess -> {
                //
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .background(colorResource(id = R.color.cardview_dark_background))
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "garbage spot id $garbageSpotId ${ garbageSpotByIdState.garbageSpot }} Screen",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                    val statusListEvent = listOf( textResource(R.string.GarbageSpotStatusListElement1).toString(),textResource(R.string.GarbageSpotStatusListElement1).toString(),textResource(R.string.GarbageSpotStatusListElement3).toString())

                    var expanded by remember { mutableStateOf(false) }
                    var selectedOptionText by remember { mutableStateOf(statusListEvent[0]) }

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
                                label = { Text(textResource(R.string.lblEventParticipateStatus).toString()) },
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

                    Button(
                        onClick = {

                                 },
                        modifier = Modifier
                            .fillMaxWidth(),

                        ) {
                        Text(text = textResource(R.string.btnUpdateParticipateOnEvent).toString())
                    }






                }


            }
        }

    }

}
