package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun HomeScreen(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    mapViewModel: MapViewModel,
    log: Logger
) {
    val log = log.withTag("HomeScreen")

    //TODO Ver se não está bug com o Guest
    LaunchedEffect(Unit) {
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
        activityViewModel.getLastActivity(authViewModel.tokenState.value)
    }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var createActivityState = activityViewModel.activityCreateUIState.collectAsState().value
    when (createActivityState) {
        is ActivityViewModel.ActivityStartUIState.Success -> {
            log.d { "Create New Activity Successful" }
            activityViewModel.setCurrentActivity(createActivityState.currentActivity)

            navController.navigate(Screen.OngoingActivity.route)
        }

        is ActivityViewModel.ActivityStartUIState.Error -> {
            log.d { "Create New Activity Failed" }
            showError = true
            errorMessage = stringResource(R.string.activityBDError)
        }
    }

    var placeholderList = mutableListOf<EventDTO>()
    var eventList by remember { mutableStateOf(emptyList<EventDTO>()) }
    var nextEvents by remember { mutableStateOf(emptyList<EventDTO>()) }
    var onGoingEvent by remember { mutableStateOf(0L) }

    var myEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    when (myEventsState) {
        is UserInfoViewModel.MyEventsUIState.Success -> {
            myEventsState.events.forEach { event ->
                placeholderList.add(event.event)
                if (event.event.status == "Começado") {
                    onGoingEvent = event.id
                }
            }

            when (placeholderList.size) {
                myEventsState.events.size -> {
                    eventList = placeholderList
                    nextEvents = getNextEvents(eventList)
                }
            }
        }

        is UserInfoViewModel.MyEventsUIState.Error -> {
            showError = true
            errorMessage = stringResource(R.string.eventsError)
        }
    }

    var lastActivityState = activityViewModel.lastActivity.collectAsState().value
    var lastActivity by remember { mutableStateOf(ActivitySerializable(0, null, null, null, null, null)) }
    when (lastActivityState) {
        is ActivityViewModel.LastActivityUIState.Success -> {
            lastActivity = lastActivityState.activity
            if(lastActivity.endDate.isNullOrEmpty()) {
                activityViewModel.setCurrentActivity(lastActivity)
            }
        }
        is ActivityViewModel.LastActivityUIState.Error -> {

        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            HomeScreenUI(
                activityViewModel,
                authViewModel,
                mapViewModel,
                navController,
                log,
                showError,
                errorMessage,
                eventList,
                nextEvents,
                lastActivity,
                onGoingEvent
            )
        }
    )
}

@Composable
fun HomeScreenUI(
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel,
    navController: NavController,
    log: Logger,
    showError: Boolean,
    errorMessage: String,
    eventList: List<EventDTO>,
    nextEvents: List<EventDTO>,
    lastActivity: ActivitySerializable,
    onGoingEvent: Long,
) {
    var isGuest = false
    if (authViewModel.tokenState.value == "0") {
        isGuest = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.default_margin))
    ) {

        if (isGuest) {
            Box(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.default_margin))
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
            ) {
                Text(
                    text = stringResource(R.string.guestOnHome),
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        if (lastActivity.endDate.isNullOrEmpty()) {
            btnOnGoingActivity(navController)
            if(onGoingEvent != 0L) {
                if (lastActivity.eventID != onGoingEvent) {
                    Text( text = stringResource(R.string.onGoingEvent),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp)
                }
            }
        } else if (onGoingEvent != 0L) {
            btnStartEventActivity(activityViewModel, authViewModel, log, onGoingEvent)
            // Activity Instead
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ){
                TextButton(
                    onClick = {
                        log.d { "Create New Activity" }

                        // Create Activity in DB
                        activityViewModel.createActivity(
                            CreateActivitySerializable(null),
                            authViewModel.tokenState.value,
                        )
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
            btnStartActivity(activityViewModel, authViewModel, log, isGuest)
        }





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

        if(nextEvents.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_spacer))
            ) {
                nextEvents.forEach { event ->
                    item {
                        val eventTime = LocalDateTime.parse(event.startDate, patternReceiver)
                        val eventString = eventTime.format(patternConverter).toString()

                        iconBoxUI(
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
        } else {
            Text(
                text = stringResource(R.string.noNextEvents),
                fontSize = dimensionResource(R.dimen.txt_medium).value.sp
            )
        }


    }
}

private fun getNextEvents(eventList: List<EventDTO>): List<EventDTO> {
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
    navController: NavController
) {
    // Button Edit Activity
    Button(
        modifier = Modifier
            .height(dimensionResource(R.dimen.btn_large))
            .fillMaxWidth()
            .background(color= Color.Red),
        onClick = {
            navController.navigate(Screen.OngoingActivity.route)
        },
    ) {
        Text(
            text = stringResource(R.string.editActivity)
        )
    }
}

@Composable
private fun btnStartActivity(
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log: Logger,
    isGuest: Boolean
) {
    // Button Start Activity
    Button(
        modifier = Modifier
            .height(dimensionResource(R.dimen.btn_large))
            .fillMaxWidth()
            .background(color= Color.Red),
        onClick = {
            log.d { "Create New Activity" }

            // Create Activity in DB
            activityViewModel.createActivity(
                CreateActivitySerializable(null),
                authViewModel.tokenState.value,
            )
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
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    log: Logger,
    eventID: Long
) {
    // Button Start Activity
    Button(
        modifier = Modifier
            .height(dimensionResource(R.dimen.btn_large))
            .fillMaxWidth()
            .background(color= Color.Red),
        onClick = {
            log.d { "Create New Activity" }

            // Create Activity in DB
            activityViewModel.createActivity(
                CreateActivitySerializable(eventID),
                authViewModel.tokenState.value,
            )
        },
    ) {
        Text(
            text = stringResource(R.string.startEventActivity)
        )
    }
}