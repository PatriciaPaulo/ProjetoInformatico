package com.example.splmobile.android.ui.main.screens.events

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.objects.events.EventDTO
import com.example.splmobile.objects.events.UserInEventDTO
import com.example.splmobile.viewmodels.*


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EventInfoScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventoId: String?,
    log: Logger
) {
    val log = log.withTag("EventInfoScreen")

    LaunchedEffect(Unit) {
        //get all events to get info
        eventViewModel.getEventsByID(eventoId!!.toLong())
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
        garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
        eventViewModel.getEquipments(authViewModel.tokenState.value)

    }
    var eventState = eventViewModel.eventByIdUIState.collectAsState().value
    var garbageSpotsState = garbageSpotViewModel.garbageSpotsUIState.collectAsState().value
    var myEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    var participateState = userViewModel.eventParticipateUIState.collectAsState().value
    var eventStatusState = eventViewModel.eventUpdateUIState.collectAsState().value
    var equipmentState = eventViewModel.equipmentUIState.collectAsState().value

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
                                garbageSpotsState,
                                equipmentState,
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
    garbageSpotsState: GarbageSpotViewModel.GarbageSpotsUIState,
    equipmentState: EventViewModel.EquipmentUIState,
    event: EventDTO,
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
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp, bottom = innerPadding.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row() {
            val image: Painter = painterResource(id = R.drawable.ic_onboarding_participate)
            Image(painter = image, contentDescription = "")

        }
        Text(text = event.name, style = MaterialTheme.typography.h5)
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = event.startDate, style = MaterialTheme.typography.body1)
            Text(text = event.status, style = MaterialTheme.typography.body1)
        }

        Text(
            text = textResource(id = R.string.lblEventInfoDescription).toString(),
            style = MaterialTheme.typography.h6
        )
        Text(text = event.description, style = MaterialTheme.typography.body1)
        Text(text = "Garbage Spots in event")
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

        ) {
            GarbageSpotsStateSection(garbageSpotsState, event, log, navController)
        }

        EquipmentStateSection(equipmentState, event)
        Spacer(Modifier.height(10.dp))
        //check if it state is to sign up or to change status of already existent sign up
        var user_event = myEventsState.events.find { ev -> ev.event.equals(event) }
        if (user_event != null) {
            log.d { "user in event update" }
            if (user_event.status.equals("Organizer")) {
                log.d { "organizer in event" }
                OrganizerSection(event, eventViewModel, authViewModel, navController)
            } else {
                log.d { "user in event sign up" }
                ParticipatorInEventSection(user_event, userViewModel, event, authViewModel)

            }

        } else {
            Log.d("EventInfoSc", "sign in event")
            SignInEventSection(event, userViewModel, authViewModel)

        }

        when (eventStatusState) {
            is EventViewModel.EventUpdateUIState.UpdateStatusSuccess -> {
                Text(
                    text = textResource(R.string.txtEventStatusUpdate).toString(),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }
            is EventViewModel.EventUpdateUIState.Loading -> {
                CircularProgressIndicator()
            }
            is EventViewModel.EventUpdateUIState.Error -> {
                Text(
                    text = textResource(R.string.txtParticipateInEventError).toString() + " - " + eventStatusState.error,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                )
            }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OrganizerSection(
    event: EventDTO,
    eventViewModel: EventViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val statusOrganizerListEvent = listOf(
        textResource(R.string.EventOrganizerStatusElement1),
        textResource(R.string.EventOrganizerStatusElement2),
        textResource(R.string.EventOrganizerStatusElement3),
        textResource(R.string.EventOrganizerStatusElement4),
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
    if (event.status != selectedOptionText) {
        Button(
            onClick = {
                eventViewModel.updateEventStatus(
                    event.id,
                    selectedOptionText,
                    authViewModel.tokenState.value
                )

            },

            ) {
            Text(text = textResource(R.string.btnUpdateParticipateOnEvent).toString())
        }
    }
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        //button for organizers to add new organizers
        if (event.status.equals(textResource(R.string.EventOrganizerStatusElement1).toString())) {
            Button(
                onClick = {
                    navController.navigate(Screen.UsersInEventList.route + "/${event.id}")

                },

                ) {
                Text(text = textResource(R.string.btnCheckParticipantsOnEvent).toString())
            }
        }

        //buton for ORGANIZERS to update event
        Button(
            onClick = {
                navController.navigate(Screen.EventEdit.route + "/${event.id}")

            },

            ) {
            Text(text = textResource(R.string.btnUpdateEvent).toString())
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ParticipatorInEventSection(
    user_event: UserInEventDTO,
    userViewModel: UserViewModel,
    event: EventDTO,
    authViewModel: AuthViewModel
) {
    val statusSignUpListEvent = listOf(
        textResource(R.string.EventSignUpStatusElement1),
        textResource(R.string.EventSignUpStatusElement3),
        textResource(R.string.EventSignUpStatusElement4),

        )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(user_event.status) }

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

@Composable
private fun SignInEventSection(
    event: EventDTO,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel
) {
    if (event.status == textResource(R.string.EventOrganizerStatusElement1).toString()) {
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

@Composable
private fun EquipmentStateSection(
    equipmentState: EventViewModel.EquipmentUIState,
    event: EventDTO
) {
    when (equipmentState) {
        is EventViewModel.EquipmentUIState.Success -> {
            Text("Equipamento")
            if (equipmentState.equipments.size > 0) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("É fornecido")
                    Text("É preciso trazer")
                }
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .height(50.dp)
                            .width(150.dp),
                        columns = GridCells.Fixed(1),
                    ) {
                        event.equipments
                            .forEachIndexed { index, card ->
                                var eq =
                                    equipmentState.equipments.find { it.id == card.equipmentID }
                                if (eq != null) {
                                    if (card.isProvided) {
                                        item(span = { GridItemSpan(1) }) {
                                            Card {
                                                Column() {
                                                    Text(text = eq.name)

                                                }
                                            }
                                        }
                                    }
                                }

                            }
                    }
                    LazyVerticalGrid(
                        modifier = Modifier
                            .width(150.dp)
                            .height(50.dp),
                        columns = GridCells.Fixed(1),
                    ) {
                        event.equipments
                            .forEachIndexed { index, card ->
                                var eq =
                                    equipmentState.equipments.find { it.id == card.equipmentID }
                                if (eq != null) {
                                    if (!card.isProvided) {
                                        item(span = { GridItemSpan(1) }) {
                                            Card {
                                                Text(text = eq.name)
                                            }
                                        }

                                    }
                                }

                            }
                    }
                }

            }else{
                Text(textResource(id = R.string.txtNoEquipmentInEvent))
            }
        }
    }
}

@Composable
private fun GarbageSpotsStateSection(
    garbageSpotsState: GarbageSpotViewModel.GarbageSpotsUIState,
    event: EventDTO,
    log: Logger,
    navController: NavController
) {
    when (garbageSpotsState) {
        is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
            if (event.garbageSpots.size == 0) {
                Text(text = textResource(id = R.string.txtNoGarbageSpotsInEvent).toString())
            } else {
                LazyHorizontalGrid(
                    modifier = Modifier
                        .height(100.dp),
                    rows = GridCells.Fixed(1),

                    ) {

                    event.garbageSpots
                        .forEachIndexed { index, card ->
                            var gs =
                                garbageSpotsState.garbageSpots.find { it.id == card.garbageSpotID }
                            if (gs != null) {
                                item(span = { GridItemSpan(1) }) {
                                    Card(
                                        Modifier.clickable {

                                            log.d { "Gs clicked -> $card" }
                                            log.d { "Navigated to new screen" }
                                            navController.navigate(Screen.GarbageSpotInfo.route + "/${card.garbageSpotID}")
                                        },
                                    ) {
                                        Column() {
                                            Text(text = gs.name)
                                            Text(text = gs.createdDate)
                                            Text(text = gs.status)
                                        }


                                    }
                                }
                            }

                        }
                }

            }

        }
        is GarbageSpotViewModel.GarbageSpotsUIState.Error -> {
            Text(text = garbageSpotsState.error)
        }
        is GarbageSpotViewModel.GarbageSpotsUIState.Loading -> {
            CircularProgressIndicator()
        }
    }
}