package com.example.splmobile.android.ui.main.screens.users


import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape

import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.users.UserSerializable
import com.example.splmobile.models.*
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun UserProfile(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    friendViewModel: FriendViewModel,
    userID: String?,
    log: Logger
) {
    val log = log.withTag("UserProfile")


    val scaffoldState = rememberScaffoldState()

    //todo test if button state working
    LaunchedEffect(Unit) {
        log.d{"get my info get my events and get my activities launched"}
        userViewModel.getUserStats(userID!!.toLong(),authViewModel.tokenState.value)
        friendViewModel.getFriendByID(userID!!.toLong(),authViewModel.tokenState.value)
    }
    var userInfoState = userViewModel.usersUIState.collectAsState().value
    var friendRequestState = friendViewModel.friendRequestUIState.collectAsState().value
    var friendsState = friendViewModel.friendsUIState.collectAsState().value

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Text("Settings?", modifier = Modifier.padding(16.dp))
            Divider()
            // Drawer items
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->

            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = innerPadding.calculateBottomPadding()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Column(modifier = Modifier
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    when(userInfoState){

                        is UserViewModel.UsersUIState.SuccessUser ->
                        {
                            log.d{"User info State -> Success"}
                            ProfileSection(
                                userInfoState.user,
                                userViewModel,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                log
                            )
                        }
                        is UserViewModel.UsersUIState.Loading -> CircularProgressIndicator()
                        is UserViewModel.UsersUIState.Error -> Text(text = "Error message - " + userInfoState.error)
                    }
                }
            }
            //todo state
            when(friendRequestState){
                is FriendViewModel.FriendRequestUIState.SuccessRequestAccepted->{
                    Text(text = "you are friends now!")
                }
                is FriendViewModel.FriendRequestUIState.SuccessRequestSent ->{
                    Text(text = "request sent!")
                }
                is FriendViewModel.FriendRequestUIState.Error->{
                    Text(text = "Error message - " + friendRequestState.error)
                }
                is FriendViewModel.FriendRequestUIState.Loading->{
                    CircularProgressIndicator()
                }
            }

            when(friendsState){
                is FriendViewModel.FriendsUIState.SuccessByUserID ->{
                    Text(text = "Friends already")
                }
                is FriendViewModel.FriendsUIState.Error->{
                    Box(contentAlignment = Alignment.Center){
                        //todo button for friend request
                        Button(onClick = {
                            friendViewModel.sendFrendRequest(userID!!.toLong(),authViewModel.tokenState.value)
                        }, shape = CutCornerShape(10)) {
                            Text(text = textResource(R.string.btnAddFriend).toString())
                        }
                    }
                }
                is FriendViewModel.FriendsUIState.Loading->{
                    CircularProgressIndicator()
                }
            }


        },
    )
}



@Composable
fun ProfileSection(
    utilizador: UserSerializable,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    log: Logger,
) {


    Column(modifier = modifier.fillMaxHeight()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RoundImage(
                image = painterResource(id = R.drawable.ic_main_profile),
                modifier = Modifier
                    .size(128.dp)

            )
            Spacer(Modifier.height(20.dp))
            StatSection(utilizador)
            Spacer(Modifier.height(20.dp))


            log.d{"In view only mode"}
            ProfileDescription(
                utilizador.name, utilizador.username
            )


        }
    }
}

@Composable
fun RoundImage(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f, matchHeightConstraintsFirst = true)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
    )
}

@Composable
fun StatSection(user: UserSerializable) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        //todo stats
        com.example.splmobile.android.ui.main.screens.ProfileStat(
            numberText = user.activities_completed,
            text = "Atividades"
        )
        Spacer(modifier = Modifier.width(40.dp))
        com.example.splmobile.android.ui.main.screens.ProfileStat(
            numberText = user.garbage_spots_created,
            text = "Garbage Spots"
        )
        Spacer(modifier = Modifier.width(40.dp))
        com.example.splmobile.android.ui.main.screens.ProfileStat(
            numberText = user.events_participated,
            text = "Eventos"
        )


    }
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Light,
            fontSize = 15.sp
        )

    }
}


@Composable
fun ProfileDescription(
    utilizadorName: String,
    utilizadorUsername: String,
) {
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = utilizadorName,
            fontWeight = FontWeight.Bold,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )

        Text(
            text = utilizadorUsername,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
    }
}




