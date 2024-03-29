package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.objects.events.EventDTO
import com.example.splmobile.objects.messages.MessageDTO
import com.example.splmobile.objects.users.FriendDTO
import com.example.splmobile.models.*
import com.google.accompanist.pager.ExperimentalPagerApi


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun ChatScreen(navController :NavController,
               messageViewModel:MessageViewModel,
               friendViewModel: FriendViewModel,
               userInfoViewModel: UserInfoViewModel,
               eventViewModel: EventViewModel,
               authViewModel: AuthViewModel) {

    LaunchedEffect(Unit){
        friendViewModel.getAllFriends(authViewModel.tokenState.value)
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
        //do this on for each of friends list
       // messageViewModel.getLastMessage(,authViewModel.tokenState.value)
    }



    var screenState = remember { mutableStateOf(false)}

    Scaffold(
        topBar = {
          Row(
              modifier = Modifier
                  .fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly
          ){
              Button(
                  onClick = {
                      screenState.value=false
                  }) {
                  Text(text = "Amigos")
              }
              Button(
                  onClick = {
                      screenState.value=true
                  }) {
                  Text(text = "Eventos")
              }
          }
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            NotificationSection(messageViewModel, authViewModel)

            when(screenState.value){
                false -> {
                    FriendsSection(
                        messageViewModel,
                        authViewModel,
                        friendViewModel,
                        navController,
                        userInfoViewModel
                    )
                }
                true -> {
                    EventsSection(
                        messageViewModel,
                        authViewModel,
                        eventViewModel,
                        navController,
                        userInfoViewModel
                    )
                }
            }
        }
    )
}

@Composable
private fun EventsSection(
    messageViewModel: MessageViewModel,
    authViewModel: AuthViewModel,
    eventViewModel: EventViewModel,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {


        EventsListSection(
           eventViewModel,
            messageViewModel,
            authViewModel,
            navController,
            userInfoViewModel
        )
    }
}
@Composable
private fun FriendsSection(
    messageViewModel: MessageViewModel,
    authViewModel: AuthViewModel,
    friendViewModel: FriendViewModel,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        NotificationSection(messageViewModel, authViewModel)

        FriendsListSection(
            friendViewModel,
            messageViewModel,
            authViewModel,
            navController,
            userInfoViewModel
        )
    }
}

@Composable
private fun NotificationSection(
    messageViewModel: MessageViewModel,
    authViewModel: AuthViewModel
) {
    when (val notificationState = messageViewModel.notiReceivedUIState.collectAsState().value) {
        is MessageViewModel.NotificationUIState.SuccessIndividual -> {
            Text("Notification received from ${notificationState.userID}")
            messageViewModel.getLastMessage(authViewModel.tokenState.value)
        }
        is MessageViewModel.NotificationUIState.SuccessEvent -> {
            Text("Notification received from ${notificationState.eventID}")
            messageViewModel.getLastEventMessage(authViewModel.tokenState.value)
        }
        MessageViewModel.NotificationUIState.Offline -> {
            messageViewModel.startConnection(authViewModel.tokenState.value)
        }
        is MessageViewModel.NotificationUIState.Error -> {
            Text(notificationState.error)
        }

    }
}

@Composable
private fun EventsListSection(
    eventViewModel: EventViewModel,
    messageViewModel: MessageViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel
) {
    var eventsListState = userInfoViewModel.myEventsUIState.collectAsState().value

    when (eventsListState) {
        is UserInfoViewModel.MyEventsUIState.Success -> {
            if(eventsListState.events.size>0){
                messageViewModel.getLastEventMessage(
                    authViewModel.tokenState.value
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp),
                ) {
                    items(eventsListState.events.size) { index ->
                        EventsListWithLastMessage(
                            eventsListState.events.get(index).event.id,
                            messageViewModel,
                            eventsListState,
                            index,
                            navController,
                            userInfoViewModel
                        )

                    }
                }
            }
            else{
                Text(text= textResource(id = R.string.txtNoEvents))
            }

        }
        is UserInfoViewModel.MyEventsUIState.Loading -> {
            CircularProgressIndicator()
        }
        is UserInfoViewModel.MyEventsUIState.Error -> {
            Text(text = "Error")
        }
    }
}



@Composable
fun EventsListWithLastMessage(eventID :Long,
    messageViewModel: MessageViewModel,
    eventsListState: UserInfoViewModel.MyEventsUIState.Success,
    index: Int,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel
) {
    val lastMessageState = messageViewModel.lastMessageUIState.collectAsState().value
    when (lastMessageState) {
        is MessageViewModel.LastMessageUIState.Success -> {

            EventsList(
                event = eventsListState.events.get(index).event,
                lastMessage = lastMessageState.message.get(index),
                navController = navController,
                userInfoViewModel = userInfoViewModel
            )


        }
        is MessageViewModel.LastMessageUIState.Error ->{

        }
    }
}

@Composable
fun EventsList(event: EventDTO,
               navController: NavController,
               lastMessage: MessageDTO,
               userInfoViewModel :UserInfoViewModel) {

    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                navController.navigate(Screen.ChatEvent.route + "/${event.id}")
            })
    ) {
        Column{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Groups,
                    "",
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.small_logo))
                )
                Column() {
                    Text(
                        text = event.name,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary
                    )

                    if(userInfoViewModel.myIdUIState.value == lastMessage.senderID){
                        Text(
                            modifier = Modifier
                                .padding(start= dimensionResource(R.dimen.textbox_margin)),
                            text = lastMessage.message,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.primary
                        )
                    }else{
                        Text(
                            modifier = Modifier
                                .padding(start= dimensionResource(R.dimen.textbox_margin)),
                            text = lastMessage.message,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun FriendsListSection(
    friendViewModel: FriendViewModel,
    messageViewModel: MessageViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel
) {
    var friendsListState = friendViewModel.friendsUIState.collectAsState().value

    when (friendsListState) {
        is FriendViewModel.FriendsUIState.SuccessAll -> {
            if(friendsListState.friends.size>0){
                messageViewModel.getLastMessage(
                    authViewModel.tokenState.value
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(friendsListState.friends.size) { index ->
                        FriendsListWithLastMessage(
                            messageViewModel,
                            friendsListState,
                            index,
                            navController,
                            userInfoViewModel
                        )

                    }

                }


            }
            else{
                Text(text= textResource(id = R.string.txtNoFriends))
            }


        }
        is FriendViewModel.FriendsUIState.Loading -> {
            CircularProgressIndicator()
        }
        is FriendViewModel.FriendsUIState.Error -> {
            Text(text = "Error")
        }
    }
}



@Composable
private fun FriendsListWithLastMessage(
    messageViewModel:MessageViewModel,
    friendsListState: FriendViewModel.FriendsUIState.SuccessAll,
    index: Int,
    navController: NavController,
    userInfoViewModel: UserInfoViewModel
) {
    val lastMessageState = messageViewModel.lastMessageUIState.collectAsState().value
    when (lastMessageState) {
        is MessageViewModel.LastMessageUIState.Success -> {
            FriendsList(
                friend = friendsListState.friends.get(index),
                lastMessage = lastMessageState.message.get(index),
                navController = navController,
                userInfoViewModel = userInfoViewModel
            )
        }
    }
}


@Composable
fun FriendsList(friend: FriendDTO, navController: NavController, lastMessage: MessageDTO,userInfoViewModel :UserInfoViewModel) {

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.small_spacer))
                .clickable(onClick = {
                    navController.navigate(Screen.ChatUser.route + "/${friend.user.id}/${friend.id}")
                })
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        "",
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.btn_small))
                    )
                    Column() {
                        Text(
                            text = friend.user.name,
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.secondary
                        )
                        Text(
                            text = friend.user.id.toString(),
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.secondary
                        )
                        if (userInfoViewModel.myIdUIState.value == lastMessage.senderID) {
                            Text(
                                modifier = Modifier
                                    .padding(start = dimensionResource(R.dimen.textbox_margin)),
                                text = lastMessage.message,
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.primary
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(start = dimensionResource(R.dimen.textbox_margin)),
                                text = lastMessage.message,
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.primary
                            )

                        }
                    }
                }
            }
        }
}
