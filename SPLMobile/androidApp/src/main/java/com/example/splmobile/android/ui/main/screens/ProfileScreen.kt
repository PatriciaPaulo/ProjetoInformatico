package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.objects.myInfo.UserSerializable
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun ProfileScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    userInfoViewModel: UserInfoViewModel,
    authViewModel: AuthViewModel,
    sharedViewModel: SharedViewModel,
    activityViewModel: ActivityViewModel,
    log: Logger
) {
    val log = log.withTag("ProfileScreen")


    val scaffoldState = rememberScaffoldState()

    var userInfoState = userInfoViewModel.myInfoUserUIState.collectAsState().value
    var usersEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    var usersActivitiesState = userInfoViewModel.myActivitiesUIState.collectAsState().value

    LaunchedEffect(Unit) {
        log.d { "get my info get my events and get my activities launched" }
        userInfoViewModel.getMyInfo(authViewModel.tokenState.value)
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
        userInfoViewModel.getMyActivities(authViewModel.tokenState.value)
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

                        is UserInfoViewModel.MyInfoUserUIState.Success -> {
                            log.d { "User info State -> Success" }
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

                    Button(
                        onClick = {
                            navController.navigate(Screen.FriendsList.route)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = textResource(R.string.btnFriends))
                    }
                }
                Text(
                    text = textResource(R.string.lblLastActivities),
                    fontStyle = MaterialTheme.typography.h6.fontStyle
                )

                MyActivitySection(usersActivitiesState, navController, log)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        text = textResource(R.string.lblLastEvents),
                        fontStyle = MaterialTheme.typography.h6.fontStyle
                    )
                    Spacer(Modifier.width(86.dp))
                    ClickableText(text = AnnotatedString(textResource(R.string.lblSeeMoreItems)),
                        style = MaterialTheme.typography.body1,
                        onClick = {
                            navController.navigate(Screen.MyEventList.route)
                        })

                }
                MyEventsSection(usersEventsState, navController, log)
            }
        },
    )
}

@ExperimentalFoundationApi
@Composable
fun MyActivitySection(
    usersActivitiesState: UserInfoViewModel.MyActivitiesUIState,
    navController: NavHostController,
    log: Logger
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (usersActivitiesState) {
            is UserInfoViewModel.MyActivitiesUIState.SuccessLast5 -> {
                usersActivitiesState.activities.forEach {
                    TextButton(onClick = {
                        navController.navigate(Screen.ActivityInfo.route+"/${it.id}")
                    }) {
                        Text(text = "Actividade ${it.id} que começou em ${it.startDate}. ")
                    }

                }


            }
            is UserInfoViewModel.MyActivitiesUIState.Loading -> CircularProgressIndicator()
            is UserInfoViewModel.MyActivitiesUIState.Error -> {
                TextButton(onClick = {}) {
                    Text(text = textResource(R.string.lblNoActivities))
                }
            }
        }


    }

}

@Composable
private fun MyEventsSection(
    usersEventsState: UserInfoViewModel.MyEventsUIState,
    navController: NavHostController,
    log: Logger
) {
    log.d("state -> ${usersEventsState}")
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (usersEventsState) {
            is UserInfoViewModel.MyEventsUIState.Success -> {
                log.d { "Get my events state -> Success" }
                var sizeOfList = 0
                if(usersEventsState.events.size == 0){
                    TextButton(onClick = {

                    }) {
                        Text(text = textResource(R.string.lblNoEvents))
                    }

                }else{
                    if (usersEventsState.events.size > 5) {
                        sizeOfList = 5
                    } else {
                        sizeOfList = usersEventsState.events.size
                    }
                    usersEventsState.events.subList(0, sizeOfList).forEach { it ->
                        TextButton(onClick = {
                            navController.navigate(Screen.EventInfo.route + "/${it.event.id}")
                        }) {
                            Text(text = "Evento ${it.event.id} com estado de ${it.status}. ")
                        }

                    }
                }


            }
            is UserInfoViewModel.MyEventsUIState.Loading -> {
                log.d { "Get my events state -> Loading" }
                CircularProgressIndicator()
            }
            is UserInfoViewModel.MyEventsUIState.Error -> {
                log.d { "Get my events state -> Error" }

            }
        }


    }
}


@Composable
fun ProfileSection(
    utilizador: UserSerializable,
    userInfoViewModel: UserInfoViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    log: Logger,
) {
    var editableState = remember { mutableStateOf(false) }
    var editableFieldsAlteredState = remember { mutableStateOf(false) }

    var btnEditarState = remember { mutableStateOf(false) }

    var utilizadorName = remember { mutableStateOf(TextFieldValue(utilizador.name)) }
    var utilizadorEmail = remember { mutableStateOf(TextFieldValue(utilizador.email)) }
    var utilizadorUsername = remember { mutableStateOf(TextFieldValue(utilizador.username)) }

    var updateUserState = userInfoViewModel.myInfoUserUpdateUIState.collectAsState().value

    if (editableFieldsAlteredState.value) {
        log.d { "User can make update request" }
        btnEditarState.value = true
    } else {
        log.d { "User cant make update request" }
        btnEditarState.value = false
    }

    if ((utilizadorName.value.text != utilizador.name && utilizadorName.value.text.isNotEmpty()) ||
        (utilizadorEmail.value.text != utilizador.email && utilizadorEmail.value.text.isNotEmpty()) ||
        (utilizadorUsername.value.text != utilizador.username && utilizadorUsername.value.text.isNotEmpty())
    ) {
        log.d { " User Info fields were changed" }
        editableFieldsAlteredState.value = true
    } else {
        log.d { " User Info fields were not changed" }
        editableFieldsAlteredState.value = false
    }

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
            StatSection(userInfoViewModel)
            Spacer(Modifier.height(20.dp))
            when (editableState.value) {
                true -> {
                    log.d { "In editable mode" }
                    EditableProfileDescription(utilizadorName, utilizadorEmail, utilizadorUsername)
                }
                false -> {
                    log.d { "In view only mode" }
                    ProfileDescription(
                        utilizador.name, utilizador.email, utilizador.username
                    )
                }
            }
            //edit state when(viewmodel state ui)
            when (updateUserState) {
                is UserInfoViewModel.MyInfoUserUpdateUIState.Success -> {
                    log.d { "User update info state -> Success" }
                    Text(
                        text = textResource(R.string.txtUserUpdatedSuccess).toString(),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                    )


                }
                is UserInfoViewModel.MyInfoUserUpdateUIState.Loading -> {
                    log.d { "User update info state -> Loading" }
                    CircularProgressIndicator()
                }

                is UserInfoViewModel.MyInfoUserUpdateUIState.Error -> {
                    log.d { "User update info state -> Error" }
                    Text(
                        text = "Error message - " + updateUserState.error,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                    )
                }
            }

            Button(
                onClick = {
                    if (editableState.value) {
                        log.d { "Values that were not updated reseted" }
                        utilizadorName.value = TextFieldValue(text = utilizador.name)
                        utilizadorEmail.value = TextFieldValue(text = utilizador.email)
                        utilizadorUsername.value = TextFieldValue(text = utilizador.username)

                    }
                    //close and open editable state
                    editableState.value = !editableState.value
                },

                modifier = Modifier.align(Alignment.End)
            ) {
                if (editableState.value) {
                    Icon(Icons.Default.Close, contentDescription = "editable")
                } else {
                    Icon(Icons.Default.Edit, contentDescription = "editable")
                }
            }

            if (editableState.value) {
                Button(
                    onClick = {
                        if (editableFieldsAlteredState.value) {
                            log.d { "Update my info request sent" }
                            userInfoViewModel.putMyInfo(
                                UserSerializable(
                                    0,
                                    utilizadorName.value.text,
                                    utilizadorEmail.value.text,
                                    utilizadorUsername.value.text,
                                    null, false
                                ),
                                authViewModel.tokenState.value
                            )
                        }
                        utilizador.email = utilizadorEmail.value.text
                        utilizador.username = utilizadorUsername.value.text
                        utilizador.name = utilizadorName.value.text
                        //change from editable to view only after request sent
                        editableState.value = false
                    },
                    enabled = btnEditarState.value,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "editable")
                }

            }


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
fun StatSection(myInfoViewModel: UserInfoViewModel) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        //todo stats
        ProfileStat(
            numberText = myInfoViewModel.myActivitiesCountUIState.collectAsState().value.toString(),
            text = "Atividades"
        )
        Spacer(modifier = Modifier.width(40.dp))
        ProfileStat(
            numberText = myInfoViewModel.myDistanceTravelled.collectAsState().value.toString() + "Km",
            text = "Distancia percorrida"
        )
        Spacer(modifier = Modifier.width(40.dp))
        ProfileStat(
            numberText = myInfoViewModel.myEventsCountUIState.collectAsState().value.toString(),
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
fun EditableProfileDescription(
    utilizadorName: MutableState<TextFieldValue>,
    utilizadorEmail: MutableState<TextFieldValue>,
    utilizadorUsername: MutableState<TextFieldValue>,

    ) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        TextField(
            value = utilizadorName.value,
            label = { Text(textResource(R.string.lblUserName).toString()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { newText ->
                utilizadorName.value = newText
            }
        )
        TextField(
            value = utilizadorEmail.value,
            label = { Text(textResource(R.string.lblUserEmail).toString()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { newText ->
                utilizadorEmail.value = newText
            }
        )
        TextField(
            value = utilizadorUsername.value,
            label = { Text(textResource(R.string.lblUserUsername).toString()) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { newText ->
                utilizadorUsername.value = newText
            }
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
    }
}





