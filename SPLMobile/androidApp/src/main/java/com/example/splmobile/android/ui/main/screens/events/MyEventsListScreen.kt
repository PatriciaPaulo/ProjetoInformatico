package com.example.splmobile.android.ui.main.screens.events


import MapAppBar
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
import com.example.splmobile.dtos.events.UserInEventDTO
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.UserInfoViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MyEventListScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    mainViewModel: MainViewModel,
    log: Logger
) {
    val log = log.withTag("MyEventListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val bottomScaffoldState = rememberBottomSheetScaffoldState()
    LaunchedEffect(Unit) {
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
    }
    var eventsListState = userInfoViewModel.myEventsUIState.collectAsState().value

    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MapAppBar(
                title = textResource(R.string.txtMyEventsList).toString(),
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
                        //todo

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


            when(eventsListState){
                is UserInfoViewModel.MyEventsUIState.Success -> {
                    LazyColumn(modifier = Modifier
                        .padding(top = 32.dp,bottom = innerPadding.calculateBottomPadding())){

                        items(eventsListState.events.size){ index ->
                            UserInEventsList(user_event = eventsListState.events.get(index), navController = navController)
                        }


                    }
                }
                is UserInfoViewModel.MyEventsUIState.Error -> {
                    Text(
                        text = textResource(R.string.txtEventError).toString() ,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                    )
                }
                is UserInfoViewModel.MyEventsUIState.Loading -> CircularProgressIndicator()
            }



        },

        )

}

@Composable
fun UserInEventsList(navController: NavHostController,user_event :UserInEventDTO){
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {
                    navController.navigate(Screen.EventInfo.route + "/${user_event.event.id}")
                },

                )


    ){
        Image(painter = painterResource(id =R.drawable.ic_main_map ), contentDescription = null )
        Column() {
            if(user_event.creator){
                Text(text = "Criador", style = MaterialTheme.typography.h6)
            }else{
                Text(text = user_event.status, style = MaterialTheme.typography.h6)
            }
            Text(text = user_event.event.name, style = MaterialTheme.typography.h6)
            Text(text = user_event.event.status, style = MaterialTheme.typography.body1)
            Text(text = user_event.event.startDate, style = MaterialTheme.typography.body2)

        }
    }
}