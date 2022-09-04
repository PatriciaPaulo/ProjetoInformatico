package com.example.splmobile.android.ui.main.screens.users

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
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.objects.events.UserInEventDTO
import com.example.splmobile.objects.users.UserDTO
import com.example.splmobile.models.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UsersInEventListScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel:  UserInfoViewModel,
    mainViewModel: MainViewModel,
    eventID: String?,
    log: Logger
) {
    val log = log.withTag("UsersInEventListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        userViewModel.getUsersInEvent(eventID!!.toLong(),authViewModel.tokenState.value)
        userViewModel.getAllUsers(authViewModel.tokenState.value)
        userInfoViewModel.getMyInfo(authViewModel.tokenState.value)
    }
    val usersEventListState = userViewModel.usersInEventUIState.collectAsState().value
    val usersListState = userViewModel.usersUIState.collectAsState().value
    val loggedInUserID = userInfoViewModel.myIdUIState.collectAsState().value
    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState

    val listSearch = remember{ mutableStateOf(emptyList<UserDTO>())}
    val usersInEventListSearch = remember{ mutableStateOf(emptyList<UserInEventDTO>())}

    val screenState = remember{ mutableStateOf(false)}

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
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
                    coroutineScope.launch {
                        var search = it
                        if(it.isNotEmpty()){
                            listSearch.value.filter{
                                it.username.contains(search) || it.name.contains(search)
                            }
                        }else{
                            userViewModel.getAllUsers(authViewModel.tokenState.value)
                        }

                    }




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

                        is UserViewModel.UsersUIState.SuccessUsers -> {
                            if(mainViewModel.searchTextState.value.isNotEmpty()){
                                listSearch.value  =  usersListState.users.filter{
                                    log.d{"mainViewModel.searchTextState.value"}
                                    it.username.contains(mainViewModel.searchTextState.value) || it.name.contains(mainViewModel.searchTextState.value)
                                }
                            }else{
                                listSearch.value = usersListState.users
                            }
                            log.d{"Get all users state -> Success"}
                            LazyColumn(modifier = Modifier
                                .padding(top = 32.dp,bottom = innerPadding.calculateBottomPadding())){

                                items(listSearch.value.size){ index ->

                                    AllUsersList(user = listSearch.value.get(index),
                                        eventID=eventID,
                                        navController = navController,
                                        usersInEventListSearch = usersInEventListSearch.value,
                                        loggedInUserID = loggedInUserID,
                                        log=log,
                                        authViewModel=authViewModel,
                                        userViewModel = userViewModel)
                                }


                            }
                        }
                        is  UserViewModel.UsersUIState.Error -> {
                            log.d{"Get all users state -> Error"}
                            Text(
                                text = textResource(R.string.txtUsersInEventError).toString() ,
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                        is  UserViewModel.UsersUIState.Loading -> CircularProgressIndicator()
                    }
                }
                false->{
                    log.d{"false"}
                    when(usersEventListState){

                        is UserViewModel.UsersInEventUIState.Success -> {
                            usersInEventListSearch.value = usersEventListState.user_events
                            log.d{"Get users in event state -> Success"}
                            LazyColumn(modifier = Modifier
                                .padding(top = 32.dp,bottom = innerPadding.calculateBottomPadding())){

                                items(usersEventListState.user_events.size){ index ->
                                    UsersList(
                                        clickedUser = usersEventListState.user_events.get(index),
                                        loggedInUserID = loggedInUserID,
                                        eventID=eventID!!.toLong(),
                                        userViewModel=userViewModel,
                                        navController = navController,
                                        usersInEventListSearch = usersInEventListSearch.value,
                                        userInfoViewModel = userInfoViewModel,
                                        authViewModel=authViewModel)
                                }


                            }
                        }
                        is  UserViewModel.UsersInEventUIState.Error -> {
                            log.d{"Get user in event state -> Error"}
                            Text(
                                text = textResource(R.string.txtUsersInEventError).toString() ,
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                        is  UserViewModel.UsersInEventUIState.Loading -> CircularProgressIndicator()
                }}
            }




        },

        )

}


@Composable
fun UsersList(
    navController: NavHostController,
    clickedUser :UserInEventDTO,
    eventID:Long,
    loggedInUserID: Long,
    userViewModel:UserViewModel,
    usersInEventListSearch: List<UserInEventDTO>,
    userInfoViewModel: UserInfoViewModel,
    authViewModel: AuthViewModel
){
    var expanded by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {
                    expanded = true
                },

                )


    ){
        Image(painter = painterResource(id =R.drawable.ic_main_map ), contentDescription = null )
        Column() {
            Text(text = clickedUser.userID.toString(), style = MaterialTheme.typography.h6)
            Text(text = clickedUser.status, style = MaterialTheme.typography.body1)
            Text(text = "Event status -" + clickedUser.event.status, style = MaterialTheme.typography.body2)


    }
        Box(contentAlignment = Alignment.CenterEnd) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // se creator -> atualizo se id != do creator
                if(usersInEventListSearch
                        .any{
                            it.creator && it.userID == loggedInUserID
                        }){
                    if(clickedUser.userID!=loggedInUserID){
                        if(clickedUser.status == "Organizer"){
                            DropdownMenuItem(onClick = {
                                //remove
                                var user_event = usersInEventListSearch.find{
                                    it.userID == clickedUser.userID
                                }
                                if(user_event!=null){
                                    userViewModel.participateStatusUpdateInEvent(eventID!!.toLong(),user_event!!.id,"Inscrito",authViewModel.tokenState.value)

                                }

                            }) {
                                Text("Remover organizador")
                            }
                            Divider()

                        }
                    }


                }

                if(clickedUser.userID!=loggedInUserID) {
                    DropdownMenuItem(onClick = {
                        navController.navigate(Screen.UserProfile.route + "/${clickedUser.userID}")
                    }) {
                        Text("Ver perfil")
                    }
                }else{
                    DropdownMenuItem(onClick = {
                        navController.navigate(BottomNavItem.Profile.route)
                    }) {
                        Text("Ver perfil")
                    }
                }
            }
            }


    }
}
@Composable
fun AllUsersList(
    navController: NavHostController,
    user: UserDTO,
    usersInEventListSearch: List<UserInEventDTO>,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    log: Logger,
    loggedInUserID: Long,
    eventID: String?
){
    var expanded by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {
                    expanded = true
                },

                )


    ){
        if(loggedInUserID != user.id) {
            Image(painter = painterResource(id =R.drawable.ic_main_map ), contentDescription = null )
            Column() {

                Text(text = user.id.toString(), style = MaterialTheme.typography.h6)
                Text(text = user.name, style = MaterialTheme.typography.body1)
                Text(text = user.username, style = MaterialTheme.typography.body1)


            }
            if(usersInEventListSearch.find{it.userID == user.id }!= null){
                usersInEventListSearch.find{it.userID == user.id }?.status?.let { Text(text = it, style = MaterialTheme.typography.h6) }

            }
        }
        var stateParticipate = userViewModel.eventParticipateUIState.collectAsState().value

        when(stateParticipate){
            is UserViewModel.EventParticipateUIState.SuccessUpdate -> {
                if(user.id == stateParticipate.userID){
                    Text(text = "Atualizado", style = MaterialTheme.typography.body1)
                }

            }
            is UserViewModel.EventParticipateUIState.SuccessOrganizer -> {
                if(user.id == stateParticipate.userID){
                    Text(text = "Adicionado", style = MaterialTheme.typography.body1)
                }
            }
        }


    }
    Box(contentAlignment = Alignment.CenterEnd) {

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                /* Handle refresh! */

                //if logged in user is the creator ->
                if(usersInEventListSearch
                        .any{
                            log.d{"$it"}
                            it.creator && it.userID == loggedInUserID
                        }){
                    //if user is already organizer
                    if(usersInEventListSearch
                            .any{
                                log.d{"$it"}
                                it.status=="Organizer" && it.userID == user.id
                            }){
                        //if user is already organizer
                        //remove
                        var user_event = usersInEventListSearch.find{
                            it.userID == user.id
                        }

                        userViewModel.participateStatusUpdateInEvent(eventID!!.toLong(),user_event!!.id,"Inscrito",authViewModel.tokenState.value)


                    }else{
                        //add
                        var user_event = usersInEventListSearch.find{
                            it.userID == user.id
                        }
                        if(user_event == null){
                            //create signup
                            userViewModel.participateInEventOrganizer(eventID!!.toLong(), user.id,authViewModel.tokenState.value)

                        }
                        else{
                            //update status
                            userViewModel.participateStatusUpdateInEvent(eventID!!.toLong(),user_event.id,"Organizer",authViewModel.tokenState.value)

                        }
                    }
                }else{
                    //not the creator but is an organizer
                    if(usersInEventListSearch
                            .any{
                                log.d{"$it"}
                                it.status=="Organizer" && it.userID == loggedInUserID
                            }){
                        //if user is already organizer
                        if(usersInEventListSearch
                                .any{
                                    log.d{"$it"}
                                    it.status=="Organizer" && it.userID == user.id
                                }){
                            //if user is already organizer
                        }else{
                            //add
                            var user_event = usersInEventListSearch.find{
                                it.userID == user.id
                            }
                            if(user_event == null){
                                //create signup
                                userViewModel.participateInEventOrganizer(eventID!!.toLong(),loggedInUserID,authViewModel.tokenState.value)

                            }
                            else{
                                //update status
                                userViewModel.participateStatusUpdateInEvent(eventID!!.toLong(),user_event.id,"Organizer",authViewModel.tokenState.value)

                            }
                        }

                    }
                }

                //profile
            }) {
                //check if in event
                //if logged in user is the creator ->
                if(usersInEventListSearch
                        .any{
                            log.d{"$it"}
                            it.creator && it.userID == loggedInUserID
                        }){
                    //if user is already organizer
                    if(usersInEventListSearch
                            .any{
                                log.d{"$it"}
                                it.status=="Organizer" && it.userID == user.id
                            }){
                        //if user is already organizer
                        Text("Remover como organizador")
                    }else{
                        Text("Adicionar como organizador")
                    }
                }else{
                    //not the creator but is an organizer
                    if(usersInEventListSearch
                            .any{
                                log.d{"$it"}
                                it.status=="Organizer" && it.userID == loggedInUserID
                            }){
                        //if user is already organizer
                        if(usersInEventListSearch
                                .any{
                                    log.d{"$it"}
                                    it.status=="Organizer" && it.userID == user.id
                                }){
                            //if user is already organizer
                        }else{
                            Text("Adicionar como organizador")
                        }
                    }
                }

            }
            Divider()
            DropdownMenuItem(onClick = {
                navController.navigate(Screen.UserProfile.route+"/${user.id}")
            }) {
                Text("Ver perfil")
            }
    }




    }
}