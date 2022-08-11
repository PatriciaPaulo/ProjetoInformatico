package com.example.splmobile.android.ui.main.screens.users

import DefaultAppBar
import MapAppBar
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.models.*
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun ChatUserScreen (navController : NavController,
                    userID:String?,
                    friendshipID:String?,
                    messageViewModel: MessageViewModel,
                    userInfoViewModel: UserInfoViewModel,
                    authViewModel: AuthViewModel,
                    userViewModel : UserViewModel
) {

    LaunchedEffect(Unit){
        messageViewModel.getMessages(friendshipID!!.toLong(),authViewModel.tokenState.value)
        userViewModel.getUserStats(userID!!.toLong(),authViewModel.tokenState.value)

    }



    val userInfoState = userViewModel.usersUIState.collectAsState().value

    Scaffold(
        topBar = {
            when(userInfoState){
                is UserViewModel.UsersUIState.SuccessUser->{
                    DefaultAppBar(
                        icon = Icons.Filled.Person,
                        title = userInfoState.user.username  ,
                        onSearchClicked = { navController.navigate(Screen.UserProfile.route+"/$userID")}
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

                NotificationState(messageViewModel, userID, authViewModel)

                MessagesSection(messageViewModel,userInfoViewModel)

                SendMessageState(messageViewModel, friendshipID, authViewModel)

                BottomSection(messageViewModel, userID, authViewModel)



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
            if(messagesState.messages.isEmpty()) {
                Text(textResource(R.string.txtNoMessagesAvailable))
            }else{
                MessagesState(messagesState, userInfoViewModel)
            }


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
            .height(525.dp),
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
        is MessageViewModel.NotificationUIState.SuccessIndividual -> {
            //Text("Notification received from ${notificationState}")

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
