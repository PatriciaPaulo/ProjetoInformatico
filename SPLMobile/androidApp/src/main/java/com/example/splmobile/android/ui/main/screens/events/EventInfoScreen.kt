package com.example.splmobile.android.ui.main.screens.events

import android.annotation.SuppressLint
import android.util.Log
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
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EventInfoScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventoId: String?,
    log: Logger
) {

    LaunchedEffect(Unit) {
        //get all events to get info
        eventViewModel.getEventsByID(eventoId!!)
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)

    }
    var eventsState = eventViewModel.eventByIdUIState.collectAsState().value
    var myEventsState = userInfoViewModel.myEventsUIState.collectAsState().value

    Log.e("event info", "Yes")
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        when (eventsState) {
            is EventViewModel.EventByIdUIState.Success -> {
                //
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .background(colorResource(id = R.color.cardview_dark_background))
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "event id $eventoId ${ eventsState.event }} Screen",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                    when(myEventsState){
                        is UserInfoViewModel.MyEventsUIState.Success -> {
                            var event = myEventsState.events.find { ev -> ev.event.id.equals(eventoId!!.toLong())}
                            if(event != null){
                                val statusSignUpListEvent = listOf( textResource(R.string.EventSignUpStatusElement1).toString(),textResource(R.string.EventSignUpStatusElement3).toString())

                                var expanded by remember { mutableStateOf(false) }
                                var selectedOptionText by remember { mutableStateOf(statusSignUpListEvent[0]) }

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
                                            statusSignUpListEvent.forEach { selectionOption ->
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
                                        eventViewModel.participateStatusUpdateInEvent(eventoId!!.toLong(),event.id,selectedOptionText,authViewModel.tokenState.value)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    ) {
                                    Text(text = textResource(R.string.btnParticipateOnEvent).toString())
                                }
                            }
                            else{
                                Log.d("info screen event","new event")
                                Button(
                                    onClick = {
                                        eventViewModel.participateInEvent(eventoId!!.toLong(),authViewModel.tokenState.value)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    ) {
                                    Text(text = textResource(R.string.btnParticipateOnEvent).toString())
                                }
                            }
                        }
                    }

                }


            }
        }

    }

}
