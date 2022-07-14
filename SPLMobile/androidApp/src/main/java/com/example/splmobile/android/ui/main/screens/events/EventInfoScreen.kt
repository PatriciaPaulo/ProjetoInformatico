package com.example.splmobile.android.ui.main.screens.events

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.models.EventViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EventInfoScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    eventoId: String?,
    log: Logger
) {

    LaunchedEffect(Unit) {
        eventViewModel.getEvents()

    }
    var eventsState = eventViewModel.eventsUIState.collectAsState().value

    Log.e("event info", "Yes")
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {


        when (eventsState) {
            is EventViewModel.EventsUIState.Success -> {
                //
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.cardview_dark_background))
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "event id $eventoId ${ eventsState.events.find { ev -> eventoId!!.toLong().equals(ev.id) }} Screen",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }

    }

}
