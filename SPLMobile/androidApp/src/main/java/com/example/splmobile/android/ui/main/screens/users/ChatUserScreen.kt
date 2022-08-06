package com.example.splmobile.android.ui.main.screens.users

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.models.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun ChatUserScreen (navController : NavController,
                    userID:String?,
                    messageViewModel: MessageViewModel,
                    userInfoViewModel: UserInfoViewModel,
                    authViewModel: AuthViewModel
) {

    LaunchedEffect(Unit){
        messageViewModel.getMessages(userID!!.toLong(),authViewModel.tokenState.value)

    }



    Scaffold(
    bottomBar = { },
    content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {


            /*
            Button(
                onClick = {
                    messageViewModel.getMessages(userID!!.toLong(),authViewModel.tokenState.value)
                },

                modifier = Modifier.align(Alignment.End)) {
                Text(text = "get all messages")
            }*/

            Text(
                text = "Chat user Screen",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            NotificationState(messageViewModel, userID, authViewModel)

            SendMessageState(messageViewModel, userID, authViewModel)

            MessagesState(messageViewModel, userInfoViewModel)

            bottomSection(messageViewModel, userID, authViewModel)



        }
    }
    )

}

@Composable
private fun SendMessageState(
    messageViewModel: MessageViewModel,
    userID: String?,
    authViewModel: AuthViewModel
) {
    val messageSendState = messageViewModel.messageCreateUIState.collectAsState().value

    when (messageSendState) {
        is MessageViewModel.MessageCreateUIState.Success -> {
            Log.d("chat with user", "create success")
            messageViewModel.getMessages(userID!!.toLong(), authViewModel.tokenState.value)


        }

    }
}

@Composable
private fun MessagesState(
    messageViewModel: MessageViewModel,
    userInfoViewModel: UserInfoViewModel
) {
    val scrollState = rememberScrollState()
    val lazyListState = rememberLazyListState(scrollState.maxValue)

    val messagesState = messageViewModel.messagesUIState.collectAsState().value
    when (messagesState) {
        is MessageViewModel.MessagesUIState.Success -> {

            MessagesSection(lazyListState,scrollState, messagesState, userInfoViewModel)

        }
    }
}

@Composable
private fun NotificationState(
    messageViewModel: MessageViewModel,
    userID: String?,
    authViewModel: AuthViewModel
) {
    val notificationState = messageViewModel.notiReceivedUIState.collectAsState().value
    when (notificationState) {
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
    }
}

@Composable
private fun MessagesSection(
    lazyListState: LazyListState,
    scrollState: ScrollState,
    messagesState: MessageViewModel.MessagesUIState.Success,
    userInfoViewModel: UserInfoViewModel
) {
    LaunchedEffect(lazyListState) {
        lazyListState.scrollToItem(scrollState.maxValue)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp), state = lazyListState
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
private fun bottomSection(
    messageViewModel: MessageViewModel,
    userID: String?,
    authViewModel: AuthViewModel
) {
    var messageToSend = remember { mutableStateOf(TextFieldValue("")) }
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
