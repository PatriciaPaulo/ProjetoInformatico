package com.example.splmobile.android.ui.main.screens.events

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.UserViewModel
import com.example.splmobile.models.UserInfoViewModel


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EventInfoScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventoId: String?,
    log: Logger
) {
    val log = log.withTag("EventInfoScreen")

    LaunchedEffect(Unit) {
        //get all events to get info
        eventViewModel.getEventsByID(eventoId!!)
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)

    }
    var eventState = eventViewModel.eventByIdUIState.collectAsState().value
    var myEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    var participateState = userViewModel.eventParticipateUIState.collectAsState().value
    var eventStatusState = eventViewModel.eventUpdateUIState.collectAsState().value


    Scaffold(
        topBar = {
            TopAppBar(
                title = {textResource(R.string.lblBarCreateEvent).toString()},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "go back",
                        )
                    }

                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        when (eventState) {
            is EventViewModel.EventByIdUIState.Success -> {
                log.d{"event id state -> Success"}
                when(myEventsState){
                    is UserInfoViewModel.MyEventsUIState.Success -> {
                        log.d{"my events state  -> Success"}
                        MainComponent(
                            navController,
                            myEventsState,
                            eventState.event,
                            userViewModel,
                            eventViewModel,
                            authViewModel,
                            participateState,
                            eventStatusState,
                            innerPadding,log
                        )
                    }
                    is UserInfoViewModel.MyEventsUIState.Error -> {
                        log.d{"my events state -> Error"}
                        log.d{"Error ->${myEventsState.error} "}
                    }
                }
            }
            is EventViewModel.EventByIdUIState.Error -> {
                log.d{"event id state -> Error"}
                log.d{"Error ->${eventState.error} "}
            }
        }

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainComponent(
    navController: NavController,
    myEventsState: UserInfoViewModel.MyEventsUIState.Success,
    event: EventSerializable,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    authViewModel: AuthViewModel,
    participateState: UserViewModel.EventParticipateUIState,
    eventStatusState: EventViewModel.EventUpdateUIState,
    innerPadding: PaddingValues,
    log: Logger
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = innerPadding.calculateBottomPadding())
            // .background(colorResource(id = R.color.cardview_dark_background))
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Row(){
            val image: Painter = painterResource(id = R.drawable.ic_onboarding_participate)
            Image(painter = image, contentDescription = "")

        }
        Text(text = event.name, style= MaterialTheme.typography.h4)
        Row(horizontalArrangement = Arrangement.SpaceAround){
            Text(text = event.startDate, style= MaterialTheme.typography.body1)
            Text(text = event.status, style= MaterialTheme.typography.body1)
        }

        Text(text = textResource(id = R.string.lblEventInfoDescription).toString(),style = MaterialTheme.typography.h6)
        Text(text = event.description, style= MaterialTheme.typography.body1)


        //check if it state is to sign up or to change status of already existent sign up
        var user_event = myEventsState.events.find { ev -> ev.event.equals(event) }
        if (user_event != null) {
            log.d{"user in event update"}
            if(user_event.status.equals("Organizer")){
                log.d{"organizer in event"}
                val statusOrganizerListEvent = listOf(
                    textResource(R.string.EventOrganizerStatusElement1).toString(),
                    textResource(R.string.EventOrganizerStatusElement2).toString(),
                    textResource(R.string.EventOrganizerStatusElement3).toString(),
                    textResource(R.string.EventOrganizerStatusElement4).toString(),
                )
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf(event.status) }

                Column(
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
                            statusOrganizerListEvent.forEach { selectionOption ->
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
                //button for organizers only to update event status
                Button(
                    onClick = {
                        eventViewModel.updateEventStatus(event.id,selectedOptionText,authViewModel.tokenState.value)

                    },
                    modifier = Modifier
                        .fillMaxWidth(),

                    ) {
                    Text(text = textResource(R.string.btnUpdateParticipateOnEvent).toString())
                }
                //button for organizers to add new organizers
                if(event.status.equals(textResource(R.string.EventOrganizerStatusElement1).toString())){
                    Button(
                        onClick = {
                            navController.navigate(Screen.UsersInEventList.route+"/${event.id}")

                        },
                        modifier = Modifier
                            .fillMaxWidth(),

                        ) {
                        Text(text = textResource(R.string.btnCheckParticipantsOnEvent).toString())
                    }
                }


            }
            else{
                log.d{"user in event sign up"}
                val statusSignUpListEvent = listOf(
                    textResource(R.string.EventSignUpStatusElement1).toString(),
                    textResource(R.string.EventSignUpStatusElement3).toString(),
                    textResource(R.string.EventSignUpStatusElement4).toString(),

                )
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf(user_event.status)}

                Column(
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
                //button for participator only to update user_event status
                Button(
                    onClick = {
                        userViewModel.participateStatusUpdateInEvent(
                            event.id,
                            user_event.id,
                            selectedOptionText,
                            authViewModel.tokenState.value
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),

                    ) {
                    Text(text = textResource(R.string.btnUpdateParticipateOnEvent).toString())
                }

            }


        } else {
            Log.d("EventInfoSc", "sign in event")
            if(event.status == textResource(R.string.EventOrganizerStatusElement1).toString()){
                Button(
                    onClick = {
                        userViewModel.participateInEvent(
                            event.id,
                            authViewModel.tokenState.value
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),

                    ) {
                    Text(text = textResource(R.string.btnParticipateOnEvent).toString())
                }
            }

        }
        when(eventStatusState){
            is EventViewModel.EventUpdateUIState.UpdateStatusSuccess -> {
                Text(
                    text = textResource(R.string.txtEventStatusUpdate).toString(),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }
            is EventViewModel.EventUpdateUIState.Loading -> {CircularProgressIndicator()}
            is EventViewModel.EventUpdateUIState.Error -> {
                Text(
                text = textResource(R.string.txtParticipateInEventError).toString() + " - " + eventStatusState.error,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
            )}
        }

        when (participateState) {
            is UserViewModel.EventParticipateUIState.SuccessParticipate -> {
                Text(
                    text = textResource(R.string.txtParticipateInEventMessage).toString(),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }
            is UserViewModel.EventParticipateUIState.SuccessUpdate -> {
                Text(
                    text = textResource(R.string.txtParticipateInEventStatusUpdateMessage).toString(),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }
            is UserViewModel.EventParticipateUIState.Loading -> CircularProgressIndicator()
            is UserViewModel.EventParticipateUIState.Error -> {
                Text(
                    text = textResource(R.string.txtParticipateInEventError).toString() + " - " + participateState.error,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }
        }




    }
}