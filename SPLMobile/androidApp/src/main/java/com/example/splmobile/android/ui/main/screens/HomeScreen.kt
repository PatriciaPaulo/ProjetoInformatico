package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.example.splmobile.objects.events.EventDTO
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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

    LaunchedEffect(Unit) {
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
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

    var myEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    when (myEventsState) {

        is UserInfoViewModel.MyEventsUIState.Success -> {
            myEventsState.events.forEach { event ->
                placeholderList.add(event.event)
            }

            when (placeholderList.size) {
                myEventsState.events.size -> {
                    eventList = placeholderList
                    nextEvents = getNextEvents(eventList)
                    println(nextEvents.size)
                }
            }
        }

        is UserInfoViewModel.MyEventsUIState.Error -> {
            showError = true
            errorMessage = stringResource(R.string.eventsError)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            HomeScreenUI(
                activityViewModel,
                authViewModel,
                mapViewModel,
                log,
                showError,
                errorMessage,
                eventList,
                nextEvents
            )
        }
    )
}

@Composable
fun HomeScreenUI(
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel,
    log: Logger,
    showError: Boolean,
    errorMessage: String,
    eventList: List<EventDTO>,
    nextEvents: List<EventDTO>,
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
                text = "Começar Atividade" //TODO Change to string res
            )
        }



        Text(text = "AAAAAAAAAAAAAAAA")

        val location = mapViewModel.getLocationLiveData()
        var parseLocationLiveData = LatLng(0.0, 0.0)
        if (location.value != null) {
            val lat = location.value!!.latitude.toDouble()
            val lng = location.value!!.longitude.toDouble()
            parseLocationLiveData = LatLng(lat, lng)
        }

        LazyRow (
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
                            ) * 10.0 ).roundToInt() / 10.0
                        else null,
                        details = eventString,
                        location = null,
                        iconPath = null,
                    )
                }
            }
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