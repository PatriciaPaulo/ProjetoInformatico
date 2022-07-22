package com.example.splmobile.android.ui.main.screens.users

import MapAppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.events.UserInEventSerializable
import com.example.splmobile.dtos.users.UserSerializable
import com.example.splmobile.models.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UsersInEventListScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    userInEventViewModel: UserInEventViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    mainViewModel: MainViewModel,
    eventID: String?,
    log: Logger
) {
    val log = log.withTag("UsersInEventListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        userInEventViewModel.getUsersInEvent(eventID!!.toLong(),authViewModel.tokenState.value)
        userInEventViewModel.getAllUsers(authViewModel.tokenState.value)
    }
    var usersEventListState = userInEventViewModel.usersInEventUIState.collectAsState().value
    var usersListState = userInEventViewModel.usersUIState.collectAsState().value
    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState

    var screenState = remember{ mutableStateOf(false)}
    val listState = rememberLazyListState()
    var selectedIndex by remember{mutableStateOf(-1L)}
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MapAppBar(
                title = textResource(R.string.lblPartipantsInEvent).toString(),
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                    mainViewModel.updateSearchTextState(newValue = it)
                },
                onCloseClicked = {
                    mainViewModel.updateSearchTextState(newValue = "")
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                    screenState.value=false



                },
                onSearchClicked = {





                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                    screenState.value=true
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->
            when(screenState.value){
                true->{
                    log.d{ "true"}
                    when(usersListState){

                        is UserInEventViewModel.UsersUIState.Success -> {
                            log.d{"Get garbage spots state -> Success"}
                            LazyColumn(modifier = Modifier
                                .padding(top = 32.dp,bottom = innerPadding.calculateBottomPadding())){

                                items(usersListState.users.size){ index ->
                                    AllUsersList(gs = usersListState.users.get(index), navController = navController)
                                }


                            }
                        }
                        is  UserInEventViewModel.UsersUIState.Error -> {
                            log.d{"Get garbage spots state -> Error"}
                            Text(
                                text = textResource(R.string.txtGarbageSpotError).toString() ,
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                        is  UserInEventViewModel.UsersUIState.Loading -> CircularProgressIndicator()
                    }
                }
                false->{
                    log.d{"false"}
                    when(usersEventListState){

                        is UserInEventViewModel.UsersInEventUIState.Success -> {
                            log.d{"Get garbage spots state -> Success"}
                            LazyColumn(modifier = Modifier
                                .padding(top = 32.dp,bottom = innerPadding.calculateBottomPadding())){

                                items(usersEventListState.user_events.size){ index ->
                                    UsersList(gs = usersEventListState.user_events.get(index), navController = navController)
                                }


                            }
                        }
                        is  UserInEventViewModel.UsersInEventUIState.Error -> {
                            log.d{"Get garbage spots state -> Error"}
                            Text(
                                text = textResource(R.string.txtGarbageSpotError).toString() ,
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                        is  UserInEventViewModel.UsersInEventUIState.Loading -> CircularProgressIndicator()
                }}
            }




        },

        )

}

@Composable
fun AllUsersList(usersListState: UserInEventViewModel.UsersUIState) {


}

@Composable
fun UserComponent(){

}
@Composable
fun UsersList(navController: NavHostController, gs :UserInEventSerializable){
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {

                },

                )


    ){
        Image(painter = painterResource(id =R.drawable.ic_main_map ), contentDescription = null )
        Column() {
            Text(text = gs.id.toString(), style = MaterialTheme.typography.h6)
            Text(text = gs.status, style = MaterialTheme.typography.body1)
            Text(text = gs.event.toString(), style = MaterialTheme.typography.body2)

        }
    }
}
@Composable
fun AllUsersList(navController: NavHostController, gs : UserSerializable){
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {

                },

                )


    ){
        Image(painter = painterResource(id =R.drawable.ic_main_map ), contentDescription = null )
        Column() {
            Text(text = gs.id.toString(), style = MaterialTheme.typography.h6)
            Text(text = gs.name, style = MaterialTheme.typography.body1)


        }
    }
}