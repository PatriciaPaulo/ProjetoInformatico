package com.example.splmobile.android.ui.main.screens.users

import MapAppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.events.UserInEventSerializable
import com.example.splmobile.dtos.users.UserSerializable
import com.example.splmobile.models.*
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FriendsListScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    friendViewModel: FriendViewModel,
    mainViewModel: MainViewModel,
    log: Logger
) {
    val log = log.withTag("FriendsListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        friendViewModel.getAllFriends(authViewModel.tokenState.value)
        userViewModel.getAllUsers(authViewModel.tokenState.value)

    }
    var friendsListBySearch = remember { mutableStateOf(emptyList<UserSerializable>())}
    var usersListState = userViewModel.usersUIState.collectAsState().value
    var friendsListState = friendViewModel.friendsUIState.collectAsState().value

    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState



    var screenState = remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MapAppBar(
                title = textResource(R.string.lblMyFriends).toString(),
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                    mainViewModel.updateSearchTextState(newValue = it)

                },
                onCloseClicked = {
                    mainViewModel.updateSearchTextState(newValue = "")
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                    screenState.value = false


                },
                onSearchClicked = {
                    coroutineScope.launch {
                        var search = it
                        if (it.isNotEmpty()) {
                            friendsListBySearch.value.filter {
                                it.username.contains(search) || it.name.contains(search)
                            }
                        } else {
                            userViewModel.getAllUsers(authViewModel.tokenState.value)
                        }

                    }


                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                    screenState.value = true
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->

            when (friendsListState) {

                is FriendViewModel.FriendsUIState.SuccessAll -> {
                    friendsListBySearch.value = friendsListState.friends

                    log.d { "Get all friends -> Success" }
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = innerPadding.calculateBottomPadding())
                    ) {

                        items(friendsListBySearch.value.size) { index ->
                            FriendssList(
                                gs = friendsListBySearch.value.get(index),
                                navController = navController
                            )
                        }


                    }
                }
                is FriendViewModel.FriendsUIState.Error -> {
                    log.d { "Get friends state -> Error" }
                    Text(
                        text = textResource(R.string.txtFriendsError).toString(),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                    )
                }
                is FriendViewModel.FriendsUIState.Loading -> CircularProgressIndicator()
            }

        }
    )

}


@Composable
fun FriendssList(navController: NavHostController, gs : UserSerializable){
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
        Image(painter = painterResource(id = R.drawable.ic_main_map ), contentDescription = null )
        Column() {
            Text(text = gs.id.toString(), style = MaterialTheme.typography.h6)
           // Text(text = gs.status, style = MaterialTheme.typography.body1)
           // Text(text = gs.event.toString(), style = MaterialTheme.typography.body2)

        }
    }
}