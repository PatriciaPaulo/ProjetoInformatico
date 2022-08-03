package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.FriendViewModel
import com.example.splmobile.models.MessageViewModel
import com.example.splmobile.websockets.MessageWebsocket
import com.google.accompanist.pager.ExperimentalPagerApi


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun ChatScreen(navController :NavController,
               messageViewModel:MessageViewModel,
               friendViewModel: FriendViewModel,

               authViewModel: AuthViewModel) {

    LaunchedEffect(Unit){
        friendViewModel.getAllFriends(authViewModel.tokenState.value)
        //do this on for each of friends list
        messageViewModel.getLastMessage(1,authViewModel.tokenState.value)
    }
    var lastMessageState = messageViewModel.messagesUIState.collectAsState().value
    var notificationState = messageViewModel.notiReceivedUIState.collectAsState().value
    Scaffold(
        bottomBar = {  },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.cardview_dark_background))
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Chat Screen",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                when(lastMessageState){
                    is MessageViewModel.MessagesUIState.SuccessLast->{
                        Text(text = "you have new message -> ${lastMessageState.message}")
                    }
                    is MessageViewModel.MessagesUIState.Success->{
                        Text("Messages loaded from user")
                    }
                }
                when(notificationState){
                    is MessageViewModel.NotificationUIState.Success->{
                        Text("Notification received from ${notificationState.userID}")
                    }
                }
                Button(
                    onClick = {
                       // MessageWebsocket().main()
                        messageViewModel.openConnection(authViewModel.tokenState.value)

                    },

                    modifier = Modifier.align(Alignment.End)) {
                    Text(text = "open connection")
                }
                Button(
                    onClick = {
                              navController.navigate(Screen.ChatUser.route+"/${1}")
                    },

                    modifier = Modifier.align(Alignment.End)) {
                    Text(text = "convo with user1")
                }
            }
        }
    )

}