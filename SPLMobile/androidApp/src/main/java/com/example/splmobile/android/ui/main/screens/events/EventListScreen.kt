package com.example.splmobile.android.ui.main.screens.events

import AppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.objects.events.EventDTO
import com.example.splmobile.viewmodels.AuthViewModel
import com.example.splmobile.viewmodels.EventViewModel
import com.example.splmobile.viewmodels.UserInfoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EventListScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    mainViewModel: MainViewModel,
    log: Logger
) {

    val log = log.withTag("EventListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        eventViewModel.getEvents()
    }
    var eventsListState = eventViewModel.eventsUIState.collectAsState().value

    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                title = textResource(R.string.lblEventListSearchBar),
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                    mainViewModel.updateSearchTextState(newValue = it)
                },
                onCloseClicked = {
                    mainViewModel.updateSearchTextState(newValue = "")
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)

                },
                onSearchClicked = {
                    coroutineScope.launch {


                    }
                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)

                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->


            when (eventsListState) {
                is EventViewModel.EventsUIState.Success -> {
                    log.d { "all events state -> Success" }
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = innerPadding.calculateBottomPadding())
                    ) {

                        items(eventsListState.events.size) { index ->
                            EventsList(
                                event = eventsListState.events.get(index),
                                navController = navController
                            )
                        }


                    }
                }
                is EventViewModel.EventsUIState.Error -> {
                    log.d { "all events state -> Error" }
                    Text(
                        text = textResource(R.string.txtEventError).toString(),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                    )
                }
                is EventViewModel.EventsUIState.Loading -> CircularProgressIndicator()
            }


        },

        )

}

@Composable
fun EventsList(navController: NavHostController, event: EventDTO) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {
                    navController.navigate(Screen.EventInfo.route + "/${event.id}")
                },

                )


    ) {
        Image(painter = painterResource(id = R.drawable.ic_main_map), contentDescription = null)
        Column() {
            Text(text = event.name, style = MaterialTheme.typography.h6)
            Text(text = event.status, style = MaterialTheme.typography.body1)
            Text(text = event.startDate, style = MaterialTheme.typography.body2)

        }
    }
}
/*
@Preview
@Composable
fun PreEventList(){
    var event = remember {
        EventSerializable(0,"Name","","","","","yesterday","","","","","")
    }
    EventsList(event = event)
}*/