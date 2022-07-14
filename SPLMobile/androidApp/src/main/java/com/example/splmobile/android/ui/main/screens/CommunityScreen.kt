package com.example.splmobile.android.ui.main.screens

import MapAppBar
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.garbageTypes.GarbageTypeSerializable
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

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
    eventViewModel: EventViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val bottomScaffoldState = rememberBottomSheetScaffoldState()
    LaunchedEffect(Unit) {
        eventViewModel.getEvents()
        garbageSpotViewModel.getGarbageSpots()

    }
    var eventsListState = eventViewModel.eventsUIState.collectAsState().value
    var garbageSpotsListState = garbageSpotViewModel.garbageSpotsUIState.collectAsState().value

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
                    when(eventsListState){
                        is EventViewModel.EventsUIState.Success -> {
                            EventsNearMeSection(navController,eventsListState.events)
                        }
                        is EventViewModel.EventsUIState.Error -> {
                            Text(text = "${eventsListState.error}")
                        }
                    }
                    //events near me section



                    //create event section
                    CreateEventSection(navController)

                    when(garbageSpotsListState){
                        is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
                            //garbagespotsnearme section
                            GarbageSpotsNearMe(garbageSpotsListState.garbageSpots)
                        }
                        is GarbageSpotViewModel.GarbageSpotsUIState.Error -> {
                            Text(text = "${garbageSpotsListState.error}")
                        }
                    }

                }
                if(buttonScreenState.value.equals(R.string.btnFriends)){
                    Text("friends screen")
                }
            }



        },

        )

}

@Composable
private fun GarbageSpotsNearMe(garbageSpots: List<GarbageSpotSerializable>) {
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
        LazyHorizontalGrid(
            modifier = Modifier
                .height(100.dp),
            rows = GridCells.Fixed(1),

            ){

            garbageSpots.subList(0,10).forEachIndexed { index, card ->
                item(span = { GridItemSpan(1) }) {
                    Card(
                    ){
                        Column() {
                            Text(text = card.name )
                            Text(text = card.status )
                        }


                    }
                }
            }
        }


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
private fun EventsNearMeSection(navController: NavHostController,
    events: List<EventSerializable>
) {
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
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {

        LazyHorizontalGrid(
            modifier = Modifier
                .height(100.dp),
            rows = GridCells.Fixed(1),

        ){
            events.filter { ev -> ev.status == "Criado" }.forEachIndexed { index, card ->
                item(span = { GridItemSpan(1) }) {
                    Card(
                        Modifier.clickable {
                            Log.d("community","$card")

                            navController.navigate(Screen.EventInfo.route + "/${card.id}")
                        },
                    ){
                        Column() {
                            Text(text = card.name )
                            Text(text = card.startDate )
                            Text(text = card.status )
                        }


                    }
                }
            }
        }



    }
}