package com.example.splmobile.android.ui.main.screens.events

import DefaultAppBar
import MapAppBar
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.models.*
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun ChatEventScreen(
    navController: NavHostController,
    eventID: String?,
    messageViewModel: MessageViewModel,
    userInfoViewModel: UserInfoViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {
    LaunchedEffect(Unit){
        messageViewModel.getEventMessages(eventID!!.toLong(),authViewModel.tokenState.value)
        eventViewModel.getEventsByID(eventID!!.toLong())
    }


    var eventState = eventViewModel.eventByIdUIState.collectAsState().value

    Scaffold(
    topBar = {
        when(eventState){
            is EventViewModel.EventByIdUIState.Success->{
                DefaultAppBar(
                    icon = Icons.Filled.Menu,
                    title = eventState.event.name  ,
                    onSearchClicked = { navController.navigate(Screen.EventInfo.route+"/$eventID")}
                )
            }
        }
    }
    ,
    content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            //NotificationState(messageViewModel, userID, authViewModel)

            //MessagesSection(messageViewModel,userInfoViewModel)

            //SendMessageState(messageViewModel, friendshipID, authViewModel)

            //BottomSection(messageViewModel, userID, authViewModel)



        }
    }
    )

}

@Composable
private fun MessagesSection(
    messageViewModel: MessageViewModel,
    userInfoViewModel: UserInfoViewModel
) {

    var messagesState = messageViewModel.messagesUIState.collectAsState().value
    when (messagesState) {
        is MessageViewModel.MessagesUIState.Success -> {
            MessagesState(messagesState, userInfoViewModel)

        }
        is MessageViewModel.MessagesUIState.Error -> Text("error")
        MessageViewModel.MessagesUIState.Loading -> Text("Loading")
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun MessagesState(
    messagesState: MessageViewModel.MessagesUIState.Success,
    userInfoViewModel: UserInfoViewModel
) {
    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val lazyListState = rememberLazyListState(scrollState.maxValue)
    Log.d("Chat user", "lsist of messages -> ${messagesState.messages.size}")
    coroutine.launch {
        lazyListState.scrollToItem(scrollState.maxValue)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp),
        state = lazyListState
    ) {
        items(messagesState.messages.size) { index ->
            MessagesList(
                message = messagesState.messages.get(index),
                userInfoViewModel = userInfoViewModel
            )
        }


    }
}

@Composable
private fun SendMessageState(
    messageViewModel: MessageViewModel,
    friendshipID: String?,
    authViewModel: AuthViewModel
) {

    when (messageViewModel.messageCreateUIState.collectAsState().value) {
        is MessageViewModel.MessageCreateUIState.Success -> {
            Log.d("chat with user", "create success")
            messageViewModel.getMessages(friendshipID!!.toLong(), authViewModel.tokenState.value)


        }
        is MessageViewModel.MessageCreateUIState.Error ->   Log.d("ChatUserScreen", "send message error")
        MessageViewModel.MessageCreateUIState.Loading -> CircularProgressIndicator()
    }
}



@Composable
private fun NotificationState(
    messageViewModel: MessageViewModel,
    userID: String?,
    authViewModel: AuthViewModel
) {
    when (val notificationState = messageViewModel.notiReceivedUIState.collectAsState().value) {
        is MessageViewModel.NotificationUIState.Success -> {
            Text("Notification received from ${notificationState}")

            messageViewModel.getMessages(
                userID!!.toLong(),
                authViewModel.tokenState.value
            )

        }
        is MessageViewModel.NotificationUIState.Error -> {
            messageViewModel.startConnection(authViewModel.tokenState.value)
        }
        MessageViewModel.NotificationUIState.Offline -> messageViewModel.startConnection(authViewModel.tokenState.value)
    }
}


@Composable
private fun BottomSection(
    messageViewModel: MessageViewModel,
    userID: String?,
    authViewModel: AuthViewModel
) {
    val messageToSend = remember { mutableStateOf(TextFieldValue("")) }
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        TextField(
            value = messageToSend.value,
            onValueChange = { messageToSend.value = it }
        )
        Button(
            onClick = {
                if (messageToSend.value.text.isNotEmpty()) {
                    messageViewModel.sendMessage(
                        IndividualMessageRequest(
                            messageToSend.value.text,
                            userID!!.toLong(),
                            "Individual"
                        ), authViewModel.tokenState.value
                    )
                    messageToSend.value = TextFieldValue("")

                }
            },

            modifier = Modifier.align(Alignment.Bottom)
        ) {
            Text(text = "sendmessage")
        }
    }
}

@Composable
fun MessagesList(message: MessageDTO, userInfoViewModel: UserInfoViewModel) {
    if(userInfoViewModel.myIdUIState.value == message.senderID){
        Row(horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = message.message,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondary
            )
        }
    }else{
        Row(horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = message.message, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
        }

    }


}
