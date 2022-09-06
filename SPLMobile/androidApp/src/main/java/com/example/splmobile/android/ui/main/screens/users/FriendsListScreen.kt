package com.example.splmobile.android.ui.main.screens.users

import AppBar
import android.annotation.SuppressLint
import android.util.Log
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
import com.example.splmobile.objects.users.FriendDTO
import com.example.splmobile.objects.users.UserDTO
import com.example.splmobile.models.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FriendsListScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    friendViewModel: FriendViewModel,
    mainViewModel: MainViewModel,
    userInfoViewModel: UserInfoViewModel,
    log: Logger
) {
    val log = log.withTag("FriendsListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        friendViewModel.getAllFriends(authViewModel.tokenState.value)
        userViewModel.getAllUsers(authViewModel.tokenState.value)


    }
    var friendsListBySearch = remember { mutableStateOf(emptyList<FriendDTO>())}
    var usersListState = userViewModel.usersUIState.collectAsState().value
    var friendsListState = friendViewModel.friendsUIState.collectAsState().value

    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState

    val screenState = remember{ mutableStateOf(false)}
    val listSearch = remember{ mutableStateOf(emptyList<UserDTO>())}


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                title = textResource(R.string.lblMyFriends).toString(),
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
                false->{
                        when (friendsListState) {

                            is FriendViewModel.FriendsUIState.SuccessAll -> {
                                if (mainViewModel.searchTextState.value.isNotEmpty()) {
                                    friendsListBySearch.value = friendsListState.friends.filter {
                                        log.d { "mainViewModel.searchTextState.value" }
                                        it.user.username.contains(mainViewModel.searchTextState.value) || it.user.name.contains(
                                            mainViewModel.searchTextState.value
                                        )
                                    }
                                } else {
                                    friendsListBySearch.value = friendsListState.friends
                                }
                                log.d { "Get all friends -> Success" }
                                if(friendsListBySearch.value.size == 0) {
                                    Text(text = "Não tens amigos!")
                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .padding(top = 32.dp, bottom = innerPadding.calculateBottomPadding())
                                ) {
                                        items(friendsListBySearch.value.size) { index ->
                                            FriendsList(
                                                friend = friendsListBySearch.value.get(index),
                                                navController = navController,
                                                friendViewModel = friendViewModel,
                                                authViewModel = authViewModel
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
                true->{
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
                                        navController = navController,
                                        log = log,
                                        authViewModel = authViewModel,
                                        friendViewModel = friendViewModel,
                                    loggedInUser =userInfoViewModel.myIdUIState.value )
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

            }
        }
    )

}
@Composable
fun AllUsersList(
    navController: NavHostController,
    user: UserDTO,
    friendViewModel: FriendViewModel,
    authViewModel: AuthViewModel,
    loggedInUser: Long,
    log: Logger,
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


    ) {
        if(user.id != loggedInUser ){
            Image(painter = painterResource(id = R.drawable.ic_main_map), contentDescription = null)
            Column() {

                //Text(text = user.id.toString(), style = MaterialTheme.typography.h6)
                Text(text = user.name, style = MaterialTheme.typography.h6)
                Text(text = user.username, style = MaterialTheme.typography.body1)


            }


            var friendRequestState= friendViewModel.friendRequestUIState.collectAsState().value

            when (friendRequestState) {
                is FriendViewModel.FriendRequestUIState.SuccessRequestSent -> {
                    if(friendRequestState.friend.user.id == user.id){
                        Text(text = "Pedido de Amizade enviado", style = MaterialTheme.typography.body1)

                    }

                }
                is  FriendViewModel.FriendRequestUIState.SuccessRequestAccepted -> {
                    if(friendRequestState.friend.user.id == user.id){
                        Text(text = "Pedido de Amizade aceite", style = MaterialTheme.typography.body1)

                    }

                }
                is  FriendViewModel.FriendRequestUIState.ErrorRequestAlreadySent -> {
                    Log.d("friendstate","error received")
                    if(friendRequestState.friend.user.id == user.id){
                        Text(text = "Pedido de Amizade já foi enviado anteriormente!", style = MaterialTheme.typography.body1)

                    }

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
                friendViewModel.sendFrendRequest(user.id,authViewModel.tokenState.value)

            }) {
                Text("Enviar Pedido de amizade")
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

@Composable
fun FriendsList(
    navController: NavHostController,
    friend: FriendDTO,
    friendViewModel: FriendViewModel,
    authViewModel: AuthViewModel
){
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {
                    navController.navigate(Screen.UserProfile.route + "/${friend.user.id}")
                },

                )


    ){
        Image(painter = painterResource(id = R.drawable.ic_main_map ), contentDescription = null )
        Column() {
            //Text(text = friend.id.toString(), style = MaterialTheme.typography.h6)
            Text(text = friend.user.username, style = MaterialTheme.typography.h6)
            Text(text = friend.user.name, style = MaterialTheme.typography.body1)


        }
    }
    var expanded by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.CenterEnd) {

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = {
                //TODO remove
                friendViewModel.removeFriend(friend.id, authViewModel.tokenState.value)

            }) {
                Text("Remove friend")
            }
            Divider()
            DropdownMenuItem(onClick = {
                //TODO NAVIGATE TO PERFIL
                navController.navigate(Screen.UserProfile.route + "/${friend.user.id}")
            }) {
                Text("Ver perfil")
            }
        }
    }
}