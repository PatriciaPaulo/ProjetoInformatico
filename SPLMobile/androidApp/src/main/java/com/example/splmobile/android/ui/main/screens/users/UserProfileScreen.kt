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
import com.example.splmobile.objects.users.UserDTO
import com.example.splmobile.viewmodels.*
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun UserProfileScreen(
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


    LaunchedEffect(Unit) {
        log.d{"get my info get my events and get my activities launched"}
        userViewModel.getUserStats(userID!!.toLong(),authViewModel.tokenState.value)
        friendViewModel.getFriendByID(userID!!.toLong(),authViewModel.tokenState.value)
    }
    val userInfoState = userViewModel.usersUIState.collectAsState().value
    val friendRequestState = friendViewModel.friendRequestUIState.collectAsState().value
    val friendsState = friendViewModel.friendsUIState.collectAsState().value

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->
            UserStatsSection(innerPadding, userInfoState, log, userViewModel)
            FriendSection(friendRequestState, friendViewModel, userID, authViewModel, friendsState)

        },
    )
}

@Composable
private fun UserStatsSection(
    innerPadding: PaddingValues,
    userInfoState: UserViewModel.UsersUIState,
    log: Logger,
    userViewModel: UserViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = innerPadding.calculateBottomPadding()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (userInfoState) {

                is UserViewModel.UsersUIState.SuccessUser -> {
                    log.d { "User info State -> Success" }
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
                is UserViewModel.UsersUIState.Error -> Text(text = "Error getting user Info ")
            }
        }
    }
}

@Composable
private fun FriendSection(
    friendRequestState: FriendViewModel.FriendRequestUIState,
    friendViewModel: FriendViewModel,
    userID: String?,
    authViewModel: AuthViewModel,
    friendsState: FriendViewModel.FriendsUIState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        FriendRequestSection(friendRequestState, friendViewModel, userID, authViewModel)

        when (friendsState) {
            is FriendViewModel.FriendsUIState.SuccessByUserID -> {

                Text(text = "Friends already")
            }
            is FriendViewModel.FriendsUIState.Error -> {
                Box(contentAlignment = Alignment.Center) {
                    Button(onClick = {
                        friendViewModel.sendFrendRequest(
                            userID!!.toLong(),
                            authViewModel.tokenState.value
                        )

                    }, shape = CutCornerShape(10)) {
                        Text(text = textResource(R.string.btnAddFriend))
                    }
                }
            }
            is FriendViewModel.FriendsUIState.Loading -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun FriendRequestSection(
    friendRequestState: FriendViewModel.FriendRequestUIState,
    friendViewModel: FriendViewModel,
    userID: String?,
    authViewModel: AuthViewModel
) {
    when (friendRequestState) {
        is FriendViewModel.FriendRequestUIState.SuccessRequestAccepted -> {
            friendViewModel.getFriendByID(
                userID!!.toLong(),
                authViewModel.tokenState.value
            )
            Text(text = "Amigos!")
        }
        is FriendViewModel.FriendRequestUIState.SuccessRequestSent -> {
            Text(text = "Pedido de amizade enviado!")
        }
        is FriendViewModel.FriendRequestUIState.ErrorRequestAlreadySent -> {
            Text(text = "Já enviou pedido de amizade" )
        }
        is FriendViewModel.FriendRequestUIState.Error -> {
            Text(text = "Erro" )
        }
        is FriendViewModel.FriendRequestUIState.Loading -> {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun ProfileSection(
    utilizador: UserDTO,
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
fun StatSection(user: UserDTO) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {

       ProfileStat(
            numberText = user.activities_completed,
            text = "Atividades"
        )
        Spacer(modifier = Modifier.width(40.dp))
       ProfileStat(
            numberText = user.garbage_spots_created,
            text = "Garbage Spots"
        )
        Spacer(modifier = Modifier.width(40.dp))
       ProfileStat(
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




