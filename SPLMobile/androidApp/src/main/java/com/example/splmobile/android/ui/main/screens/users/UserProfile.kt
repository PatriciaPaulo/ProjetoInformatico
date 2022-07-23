package com.example.splmobile.android.ui.main.screens.users


import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

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
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.myInfo.UserSerializable
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.UserInEventViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun UserProfile(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    userInEventViewModel: UserInEventViewModel,
    authViewModel: AuthViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {
    val log = log.withTag("UserProfile")

/*
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        log.d{"get my info get my events and get my activities launched"}

    }

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

                        is UserInfoViewModel.MyInfoUserUIState.Success ->
                        {
                            log.d{"User info State -> Success"}
                            ProfileSection(
                                userInfoState.data,
                                userInfoViewModel,
                                authViewModel,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                log
                            )
                        }
                        is UserInfoViewModel.MyInfoUserUIState.Loading -> CircularProgressIndicator()
                        is UserInfoViewModel.MyInfoUserUIState.Error -> Text(text = "Error message - " + userInfoState.error)
                    }
                }
            }
        },
    )
}



@Composable
fun ProfileSection(
    utilizador: UserSerializable,
    userInfoViewModel: UserInfoViewModel,
    authViewModel: AuthViewModel,
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
            StatSection()
            Spacer(Modifier.height(20.dp))


            log.d{"In view only mode"}
            ProfileDescription(
                utilizador.name, utilizador.email, utilizador.username
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
fun StatSection(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        //todo stats
        ProfileStat(numberText = "601", text = "Atividades")
        Spacer(modifier = Modifier.width(40.dp))
        ProfileStat(numberText = "72", text = "Distancia percorrida")
        Spacer(modifier = Modifier.width(40.dp))
        ProfileStat(numberText = "100K", text = "Eventos")

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
    utilizadorEmail: String,
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
            text = utilizadorEmail,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
        Text(
            text = utilizadorUsername,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight
        )
    }*/
}




