package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.myInfo.UserSerializable
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
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
    log: Logger
) {

    //android view model states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    var userInfoState = userInfoViewModel.myInfoUserUIState.collectAsState().value
    var usersEventsState = userInfoViewModel.myEventsUIState.collectAsState().value
    var usersActivitiesState = userInfoViewModel.myActivitiesUIState.collectAsState().value
    LaunchedEffect(Unit) {
        userInfoViewModel.getMyInfo(authViewModel.tokenState.value)
        userInfoViewModel.getMEvents(authViewModel.tokenState.value)
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
            
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Column(modifier = Modifier
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    when(userInfoState){
                        is UserInfoViewModel.MyInfoUserUIState.Success ->
                        {
                            ProfileSection(
                                userInfoState.data,
                                userInfoViewModel,
                                authViewModel,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally))

                            


                        }
                    }
                }
                Text(text = "Your last Activities", fontStyle = MaterialTheme.typography.h6.fontStyle)

                Column(modifier = Modifier
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    when(usersActivitiesState){
                        is UserInfoViewModel.MyActivitiesUIState.SuccessLast5 ->
                        {

                            usersActivitiesState.activities.forEach {
                                TextButton(onClick = {}){
                                    Text(text = "Evento ${it.id} que começou em ${it.startDate}. ")
                                }

                            }


                        }
                        is UserInfoViewModel.MyActivitiesUIState.Error -> {
                            TextButton(onClick = {}){
                                Text(text = "No Activities Available!. ")
                            }
                        }
                    }
                    


                }
                Text(text = "Your last Events", fontStyle = MaterialTheme.typography.h6.fontStyle)
                Column(modifier = Modifier
                    .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    when(usersEventsState){
                        is UserInfoViewModel.MyEventsUIState.SuccessLast5 ->
                        {
                            usersEventsState.events.forEach {
                                TextButton(onClick = {}){
                                    Text(text = "Evento ${it.id} com estado de ${it.status}. ")
                                }

                            }

                        }
                        is UserInfoViewModel.MyEventsUIState.Error -> {
                            TextButton(onClick = {}){
                                Text(text = "No Events Available!. ")
                            }
                        }
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
) {
    var editableState = remember { mutableStateOf(false) }
    var editableFieldsAlteredState = remember { mutableStateOf(false) }

    var btnEditarState = remember{ mutableStateOf(false)}

    var utilizadorName = remember { mutableStateOf(TextFieldValue(utilizador.name)) }
    var utilizadorEmail = remember { mutableStateOf(TextFieldValue(utilizador.email)) }
    var utilizadorUsername = remember { mutableStateOf(TextFieldValue(utilizador.username)) }

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
            when (editableState.value) {
                true-> {
                      EditableProfileDescription(utilizadorName, utilizadorEmail, utilizadorUsername,editableFieldsAlteredState)
                }
                false -> {
                    ProfileDescription(
                        utilizador.name, utilizador.email, utilizador.username
                    )
                }
            }

            if(editableFieldsAlteredState.value){
                btnEditarState.value = true
            }else{
                btnEditarState.value = false
            }

            if((utilizadorName.value.text != utilizador.name && utilizadorName.value.text.isNotEmpty())||
                (utilizadorEmail.value.text != utilizador.email && utilizadorEmail.value.text.isNotEmpty())||
                (utilizadorUsername.value.text != utilizador.username && utilizadorUsername.value.text.isNotEmpty())){
                editableFieldsAlteredState.value = true
            }else{
                editableFieldsAlteredState.value = false
            }
            Button(
                onClick = {
                    if(editableState.value){
                        utilizadorName.value = TextFieldValue(text = utilizador.name)
                        utilizadorEmail.value = TextFieldValue(text = utilizador.email)
                        utilizadorUsername.value =  TextFieldValue(text = utilizador.username)

                    }
                    editableState.value = !editableState.value },

                modifier = Modifier.align(Alignment.End)) {
                if(editableState.value){
                    Icon(Icons.Default.Close, contentDescription = "editable")
                }else{
                    Icon(Icons.Default.Edit, contentDescription = "editable")
                }
                }

            if(editableState.value){
                Button(
                    onClick = {
                        if(editableFieldsAlteredState.value){
                            userInfoViewModel.putMyInfo(
                                UserSerializable(
                                    0,
                                    utilizadorName.value.text,
                                    utilizadorEmail.value.text,
                                    utilizadorUsername.value.text),
                                authViewModel.tokenState.value)
                        }
                              },
                    enabled = btnEditarState.value,
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Icon(Icons.Default.Check, contentDescription = "editable")
                    }

            }
            //edit state when(viewmodel state ui)


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
fun EditableProfileDescription(
    utilizadorName: MutableState<TextFieldValue>,
    utilizadorEmail: MutableState<TextFieldValue>,
    utilizadorUsername: MutableState<TextFieldValue>,
    editableFieldsAlteredState: MutableState<Boolean>,
) {
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
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

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier
) {
    val minWidth = 95.dp
    val height = 30.dp
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        ActionButton(
            text = "Following",
            icon = Icons.Default.KeyboardArrowDown,
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth)
                .height(height)
        )
        ActionButton(
            text = "Message",
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth)
                .height(height)
        )
        ActionButton(
            text = "Email",
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth)
                .height(height)
        )
        ActionButton(
            icon = Icons.Default.KeyboardArrowDown,
            modifier = Modifier
                .size(height)
        )
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(6.dp)
    ) {
        if(text != null) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
        if(icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun ActivitySection(
    posts: List<Painter>,
    modifier: Modifier = Modifier
) {
    

}

@ExperimentalFoundationApi
@Composable
fun NextEventsSection(
    posts: List<Painter>,
    modifier: Modifier = Modifier
) {


}