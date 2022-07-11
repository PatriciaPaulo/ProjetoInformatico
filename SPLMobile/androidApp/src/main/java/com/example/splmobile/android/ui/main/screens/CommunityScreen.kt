package com.example.splmobile.android.ui.main.screens

import MapAppBar
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.myInfo.UserSerializable
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun CommunityScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val bottomScaffoldState = rememberBottomSheetScaffoldState()


    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MapAppBar(
                title = textResource(R.string.lblCommunitySearchBar).toString(),
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                    mainViewModel.updateSearchTextState(newValue = it)
                },
                onCloseClicked = {
                    mainViewModel.updateSearchTextState(newValue = "")
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)

                },
                onSearchClicked = {
                    coroutineScope.launch {


                    }
                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)

                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->


            var buttonScreenState = remember{ mutableStateOf(R.string.btnCommunity)}

            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment =  Alignment.CenterHorizontally) {
                // Apply the padding globally
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment =  Alignment.CenterVertically

                ){
                    Button(
                        onClick = {
                            buttonScreenState.value = R.string.btnCommunity
                        },
                        modifier = Modifier.align(Alignment.CenterVertically),

                        ) {
                        Text(text = textResource(R.string.btnCommunity).toString())
                    }
                    Button(
                        onClick = {
                            buttonScreenState.value = R.string.btnFriends
                        },
                        modifier = Modifier.align(Alignment.CenterVertically))  {
                        Text(text = textResource(R.string.btnFriends).toString())
                    }


                }

                if(buttonScreenState.value.equals(R.string.btnCommunity)){
                    //events near me section
                    EventsNearMeSection()


                    //create event section
                    CreateEventSection(navController)


                    //garbagespotsnearme section
                    GarbageSpotsNearMe()
                }
                if(buttonScreenState.value.equals(R.string.btnFriends)){
                    Text("friends screen")
                }
            }



        },

        )

}

@Composable
private fun GarbageSpotsNearMe() {
    Spacer(modifier = Modifier.height(32.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {

        Text(text = textResource(R.string.lblGarbageSpotsNearMe).toString())
        ClickableText(text = AnnotatedString(textResource(R.string.lblSeeMoreItems).toString()),
            style = MaterialTheme.typography.body1,
            onClick = {

            })

    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        //LazyVerticalGrid(columns = , content = )
        Text(text = "LOCAL DE LIXO")
        Text(text = "LOCAL DE LIXO")
        Text(text = "LOCAL DE LIXO")


    }
}

@Composable
private fun CreateEventSection(
    navController: NavHostController
) {
    Spacer(modifier = Modifier.height(32.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(text = textResource(R.string.lblNoEventForMe).toString())
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {

        Button(
            onClick = {
                navController.navigate(Screen.CreateEvent.route)
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()
        ) {
            Text(text = textResource(R.string.btnCreateEvent).toString())
        }

    }
}

@Composable
private fun EventsNearMeSection() {
    Spacer(modifier = Modifier.height(32.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(text = textResource(R.string.lblEventsNearMe).toString())
        ClickableText(text = AnnotatedString(textResource(R.string.lblSeeMoreItems).toString()),
            style = MaterialTheme.typography.body1,
            onClick = {

            })


    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        //LazyVerticalGrid(columns = , content = )
        Text(text = "EVENTO")
        Text(text = "EVENTO")
        Text(text = "EVENTO")

    }
}