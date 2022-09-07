package com.example.splmobile.android.ui.main.screens.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.patternConverter
import com.example.splmobile.android.patternReceiver
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.objects.activities.ActivitySerializable
import com.example.splmobile.objects.activities.ExplicitGarbageInActivityDTO
import java.time.LocalDateTime

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ActivityInfoScreen(
    navController: NavController,
    activityViewModel: ActivityViewModel,
    eventViewModel: EventViewModel,
    authViewModel: AuthViewModel,
    activityID: String?,
    log: Logger
) {
    val log = log.withTag("ActivityInfoScreen")

    var garbageList by remember { mutableStateOf(emptyList<ExplicitGarbageInActivityDTO>()) }

    LaunchedEffect(Unit) {
        activityViewModel.getActivityByID(
            activityID!!.toLong(),
            authViewModel.tokenState.value
        )

        activityViewModel.getActivityTypes()
        activityViewModel.getGarbageInActivity(
            activityID!!.toLong(),
            authViewModel.tokenState.value
        ) { gl ->
            garbageList = gl
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { "Informação de Atividade" },
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
    ) {
        var activityByIDState = activityViewModel.activityByID.collectAsState().value
        when(activityByIDState) {
            is ActivityViewModel.GetActivityByID.Success -> {
                if (activityByIDState.activity.eventID != null) {
                    eventViewModel.getEventsByID(activityByIDState.activity.eventID!!)

                    ActivityInfoUI(
                        activityByIDState.activity,
                        garbageList,
                        eventViewModel,
                        activityViewModel
                    )
                }

            }
        }
    }

}

@Composable
private fun ActivityInfoUI(
    activity : ActivitySerializable,
    garbageList : List<ExplicitGarbageInActivityDTO>,
    eventViewModel: EventViewModel,
    activityViewModel: ActivityViewModel
) {
    var activityType by remember { mutableStateOf(activity.activityTypeID.toString()) }
    var activityEvent by remember { mutableStateOf("") }

    if (activity.eventID != null) {
        activityEvent = activity.activityTypeID.toString()
    }

    var eventByIDState = eventViewModel.eventByIdUIState.collectAsState().value
    when(eventByIDState) {
        is EventViewModel.EventByIdUIState.Success -> {
            activityEvent = eventByIDState.event.name
        }
    }

    var activityTypeState = activityViewModel.activityTypeUIState.collectAsState().value
    when(activityTypeState) {
        is ActivityViewModel.ActivityTypeUIState.Success -> {
            activityTypeState.activityTypes.forEach { at ->
                if (at.id == activity.activityTypeID) {
                    activityType = at.name
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.default_margin)),
            ){
        Text("Tipo de Atividade", style = MaterialTheme.typography.h5)
        Text(activityType, style = MaterialTheme.typography.body1)
        Spacer(
            modifier = Modifier
                .size(dimensionResource(R.dimen.big_spacer))
        )

        if(!activityEvent.isNullOrEmpty()) {
            Text("Evento", style = MaterialTheme.typography.h5)
            Text(activityEvent, style = MaterialTheme.typography.body1)
            Spacer(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.big_spacer))
            )
        }

        val startDate = LocalDateTime.parse(activity.startDate, patternReceiver)
        val startString = startDate.format(patternConverter).toString()

        val endDate = LocalDateTime.parse(activity.endDate, patternReceiver)
        val endString = startDate.format(patternConverter).toString()

        Text("${startString} - ${endString}", style = MaterialTheme.typography.body1)
        Spacer(
            modifier = Modifier
                .size(dimensionResource(R.dimen.big_spacer))
        )

        Text("Lixo Apanhado na Atividade", style = MaterialTheme.typography.h5)

            LazyColumn(
                modifier = Modifier
                    .padding(start = 8.dp)
            ){
                item {
                    garbageList.forEach { gb ->
                        var gb_unit = ""
                        if(gb.amount > 1) {
                            gb_unit = gb.unit + "s"
                        }
                        Text(
                            "${gb.garbage} - ${gb.amount} ${gb_unit}")
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                }
            }

    }
}