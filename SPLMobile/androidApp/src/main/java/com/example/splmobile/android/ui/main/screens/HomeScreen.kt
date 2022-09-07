package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.patternConverter
import com.example.splmobile.android.patternReceiver
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.iconBoxUI
import com.example.splmobile.android.ui.main.screens.activities.calculateDistance
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.objects.activities.CreateActivitySerializable
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.example.splmobile.objects.activities.ActivitySerializable
import com.example.splmobile.objects.events.EventDTO
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen (
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventViewModel: EventViewModel,
    mapViewModel: MapViewModel,
    log: Logger
) {
    val log = log.withTag("HomeScreen")

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            HomeScreenUI(
                navController,
                activityViewModel,
                authViewModel,
                userInfoViewModel,
                eventViewModel,
                mapViewModel,
                log)
        }
    )

}

@Composable
private fun HomeScreenUI(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventViewModel: EventViewModel,
    mapViewModel: MapViewModel,
    log: Logger
) {
    log.d { "HomeScreen UI" }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.default_margin))
            ) {

        ActivityButtonUI(navController, activityViewModel, authViewModel, log)

        Spacer(
            modifier = Modifier
                .size(dimensionResource(R.dimen.big_spacer))
        )
        EventListCompose(
            userInfoViewModel,
            eventViewModel,
            authViewModel,
            navController,
            mapViewModel,
            log
        )
    }
}

@Composable
fun MyNextEventsUI(
    mapViewModel: MapViewModel,
    nextEvents: List<EventDTO>,
    navController: NavController,
    log: Logger
) {
    val location = mapViewModel.getLocationLiveData()
    var parseLocationLiveData = LatLng(0.0, 0.0)
    if (location.value != null) {
        val lat = location.value!!.latitude.toDouble()
        val lng = location.value!!.longitude.toDouble()
        parseLocationLiveData = LatLng(lat, lng)
    }

    Text(
        text = stringResource(R.string.lblNextEvents),
        fontSize = dimensionResource(R.dimen.title).value.sp,
    )

    if (nextEvents.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_spacer))
        ) {
            nextEvents.forEach { event ->
                item {
                    val eventTime = LocalDateTime.parse(event.startDate, patternReceiver)
                    val eventString = eventTime.format(patternConverter).toString()
                    Card (modifier = Modifier
                        .clickable { navController.navigate(Screen.EventInfo.route + "/${event.id}") }) {
                        iconBoxUI(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Screen.EventInfo.route + "/${event.id}")
                                },
                            name = event.name,
                            distance = if (location != null) (calculateDistance(
                                parseLocationLiveData,
                                LatLng(event.latitude.toDouble(), event.longitude.toDouble())
                            ) * 10.0).roundToInt() / 10.0
                            else null,
                            details = eventString,
                            location = null,
                            iconPath = null,
                        )
                    }
                }
            }
        }
    } else {
        Text(
            text = stringResource(R.string.noNextEvents),
            fontSize = dimensionResource(R.dimen.txt_medium).value.sp
        )
    }
}

@Composable
fun ActivityButtonUI(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log: Logger
) {
    var isGuest = false
    if (authViewModel.tokenState.value == "0") {
        isGuest = true
    }

    var lastActivity by remember { mutableStateOf(ActivitySerializable(0, null, null, null, null, "Not Going")) }

    if(lastActivity.id == 0L) {
        activityViewModel.getLastActivity(authViewModel.tokenState.value)
    }
    log.d { "Last Activity Compose Called" }

    val lastActivityState = activityViewModel.lastActivity.collectAsState().value
    log.d { lastActivityState.toString() }
    log.d { "lastActivity -> $lastActivity" }
    when (lastActivityState) {
        is ActivityViewModel.LastActivityUIState.Success -> {
            log.d { "Last Activity Success" }
            lastActivity = lastActivityState.activity
        }
        is ActivityViewModel.LastActivityUIState.Error -> { }
        is ActivityViewModel.LastActivityUIState.Loading -> {

        }
    }

    val ongoingEvent = 0L

    log.d { "ongoing event -> $ongoingEvent" }
    log.d { "last activity -> $lastActivity" }

    if (lastActivity.endDate.isNullOrEmpty()) {
        btnOnGoingActivity(navController, lastActivity.id)
        if (ongoingEvent != 0L) {
            if (lastActivity.eventID != ongoingEvent) {
                Text(
                    text = stringResource(R.string.onGoingEvent),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp
                )
            }
        }
    } else if (ongoingEvent != 0L) {
        btnStartEventActivity(navController, activityViewModel, authViewModel, log, ongoingEvent)
        // Activity Instead
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            TextButton(
                onClick = {
                    log.d { "Create New Single Activity" }

                    // Create Activity in DB
                    activityViewModel.createActivity(
                        CreateActivitySerializable(null),
                        authViewModel.tokenState.value,
                    ) {
                        navController.navigate(Screen.OngoingActivity.route + "/$it")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = AnnotatedString(
                        text = stringResource(R.string.noEvent),
                        spanStyle = SpanStyle(color = MaterialTheme.colors.onBackground)
                    ).plus(
                        AnnotatedString(
                            text = " " + stringResource(R.string.doActivity),
                        )
                    ),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp,
                )
            }
        }
    } else {
        btnStartActivity(navController, activityViewModel, authViewModel, log, isGuest)
    }
}



@Composable
fun EventListCompose(
    userInfoViewModel: UserInfoViewModel,
    eventViewModel: EventViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    mapViewModel: MapViewModel,
    log: Logger,
) {
    var myEventsList = mutableListOf<EventDTO>()

    if(myEventsList.isEmpty()) {
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
    }

    log.d { "Event Composable Called" }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val myEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    when (myEventsState) {

        is UserInfoViewModel.MyEventsUIState.Success -> {
            log.d { "Get Events Sucessfull" }
            run breaking@{
                myEventsState.events.forEach { event ->
                    myEventsList.add(event.event)
                    if (event.event.status == "Começado") {
                        eventViewModel.setOngoingEvent(event.id)
                        return@breaking
                    }
                }
            }
            var returnList = getNextEvents(myEventsList)
            log.d { "Events Return List -> ${returnList.listIterator()}" }
            log.d { "Calling Next Events" }
            MyNextEventsUI(mapViewModel, returnList, navController, log)
        }

        is UserInfoViewModel.MyEventsUIState.Error -> {
            showError = true
            errorMessage = stringResource(R.string.eventsError)
        }
    }
}





private fun getNextEvents(eventList: MutableList<EventDTO>): List<EventDTO> {
    val currentTime = LocalDateTime.now()
    eventList.sortedBy { event ->
        val eventTime = LocalDateTime.parse(event.startDate, patternReceiver)
        abs(
            eventTime!!.toInstant(ZoneOffset.UTC)
                .toEpochMilli() - currentTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        )
    }

    val returnList = arrayListOf<EventDTO>()
    var i = 0
    while (returnList.size != 5 && returnList.size != eventList.size && i != eventList.size) {
        if (LocalDateTime.parse(eventList[i].startDate, patternReceiver).toInstant(ZoneOffset.UTC)
                .toEpochMilli()
            > currentTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        ) {
            returnList.add(eventList[i])
        }

        i += 1
    }

    return returnList
}


@Composable
private fun btnOnGoingActivity(
    navController: NavController,
    activityID: Long
) {
    // Button Edit Activity
    Button(
        modifier = Modifier
            .height(dimensionResource(R.dimen.btn_large))
            .fillMaxWidth(),
        onClick = {
            navController.navigate(Screen.OngoingActivity.route + "/${activityID}")
        },
    ) {
        Text(
            text = stringResource(R.string.editActivity)
        )
    }


}

@Composable
private fun btnStartActivity(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log: Logger,
    isGuest: Boolean,
) {
    // Button Start Activity
    Button(
        modifier = Modifier
            .height(dimensionResource(R.dimen.btn_large))
            .fillMaxWidth(),
        onClick = {
            log.d { "Create New Single Activity" }

            // Create Activity in DB
            activityViewModel.createActivity(
                CreateActivitySerializable(null),
                authViewModel.tokenState.value,
            ) { id ->
                navController.navigate(Screen.OngoingActivity.route + "/$id")
            }

        },
        enabled = !isGuest
    ) {
        Text(
            text = stringResource(R.string.startActivity)
        )
    }
}

@Composable
private fun btnStartEventActivity(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log: Logger,
    eventID: Long
) {
    // Button Start Activity
    Button(
        modifier = Modifier
            .height(dimensionResource(R.dimen.btn_large))
            .fillMaxWidth(),
        onClick = {
            log.d { "Create New Event Activity" }

            // Create Activity in DB
            activityViewModel.createActivity(
                CreateActivitySerializable(eventID),
                authViewModel.tokenState.value,
            ) {
                navController.navigate(Screen.OngoingActivity.route + "/$it")
            }
        },
    ) {
        Text(
            text = stringResource(R.string.startEventActivity)
        )
    }
}
