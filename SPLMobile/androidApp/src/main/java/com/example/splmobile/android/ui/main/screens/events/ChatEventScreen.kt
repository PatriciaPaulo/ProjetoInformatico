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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.sharp.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.messages.EventMessageRequest
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
        userViewModel.getAllUsers(authViewModel.tokenState.value)
    }


    var eventState = eventViewModel.eventByIdUIState.collectAsState().value

    Scaffold(
    topBar = {
        when(eventState){
            is EventViewModel.EventByIdUIState.Success->{
                DefaultAppBar(
                    icon = Icons.Filled.Menu,
                    title = eventState.event.name + " - " + eventID  ,
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

            NotificationState(messageViewModel, eventID, authViewModel)

            MessagesSection(messageViewModel,userInfoViewModel,userViewModel)

            SendMessageState(messageViewModel, eventID, authViewModel)

            BottomSection(messageViewModel, eventID, authViewModel)



        }
    }
    )

}

@Composable
private fun MessagesSection(
    messageViewModel: MessageViewModel,
    userInfoViewModel: UserInfoViewModel,
    userViewModel: UserViewModel
) {

    val messagesState = messageViewModel.messagesUIState.collectAsState().value
    when (messagesState) {
        is MessageViewModel.MessagesUIState.Success -> {
            MessagesState(messagesState, userInfoViewModel, userViewModel )
        }
        is MessageViewModel.MessagesUIState.Error -> Text(textResource(R.string.txtNoMessagesAvailable))
        MessageViewModel.MessagesUIState.Loading -> Text("Loading")
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun MessagesState(
    messagesState: MessageViewModel.MessagesUIState.Success,
    userInfoViewModel: UserInfoViewModel,
    userViewModel: UserViewModel
) {
    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val lazyListState = rememberLazyListState(scrollState.maxValue)
    Log.d("Chat user", "list of messages in event-> ${messagesState.messages.size}")
    coroutine.launch {
        lazyListState.scrollToItem(scrollState.maxValue)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(525.dp),
        state = lazyListState
    ) {
        items(messagesState.messages.size) { index ->
            MessagesList(
                message = messagesState.messages.get(index),
                userInfoViewModel = userInfoViewModel,
                userViewModel = userViewModel
            )
        }


    }
}

@Composable
private fun SendMessageState(
    messageViewModel: MessageViewModel,
    eventID: String?,
    authViewModel: AuthViewModel
) {

    when (messageViewModel.messageCreateUIState.collectAsState().value) {
        is MessageViewModel.MessageCreateUIState.Success -> {
            Log.d("chat with user", "create success")
            messageViewModel.getEventMessages(eventID!!.toLong(), authViewModel.tokenState.value)


        }
        is MessageViewModel.MessageCreateUIState.Error ->   Log.d("ChatUserScreen", "send message error")
        MessageViewModel.MessageCreateUIState.Loading -> CircularProgressIndicator()
    }
}



@Composable
private fun NotificationState(
    messageViewModel: MessageViewModel,
    eventID: String?,
    authViewModel: AuthViewModel
) {
    when (val notificationState = messageViewModel.notiReceivedUIState.collectAsState().value) {
        is MessageViewModel.NotificationUIState.SuccessEvent -> {
            //Text("Notification received from ${notificationState}")
            println( "Received notificaiton ${notificationState.eventID }")
            messageViewModel.getEventMessages(
                eventID!!.toLong(),
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
    eventID: String?,
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
                    messageViewModel.sendEventMessage(
                        EventMessageRequest(
                            messageToSend.value.text,
                            eventID!!.toLong(),
                            "Event"
                        ), authViewModel.tokenState.value
                    )
                    messageToSend.value = TextFieldValue("")

                }
            },

            modifier = Modifier.align(Alignment.Bottom)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Message",
            )
        }
    }
}

@Composable
fun MessagesList(message: MessageDTO, userInfoViewModel: UserInfoViewModel,userViewModel: UserViewModel) {
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
            var usersState = userViewModel.usersUIState.collectAsState().value
            when(usersState){
                is UserViewModel.UsersUIState.SuccessUsers->{
                    val user = usersState.users.find { it.id == message.senderID }
                    if(user!=null){
                        Text(text = user.username + " - ", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
                    }
                }
            }
             Text(text = message.message, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.primary)
        }

    }


}
