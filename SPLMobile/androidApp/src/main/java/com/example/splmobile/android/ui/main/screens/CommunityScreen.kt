package com.example.splmobile.android.ui.main.screens

import MapAppBar
import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.main.screens.activities.calculateDistance
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.events.EventDTO
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.dtos.myInfo.LocationDetails
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.maps.model.LatLng

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
    mapViewModel: MapViewModel,
    log: Logger
) {
    val log = log.withTag("CommunityScreen")

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val bottomScaffoldState = rememberBottomSheetScaffoldState()
    LaunchedEffect(Unit) {
        log.d{"get events and get garbage spots launched"}
        eventViewModel.getEvents()
        garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)

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
                    when(eventsListState){
                        is EventViewModel.EventsUIState.Success -> {
                            EventsNearMeSection(navController,eventsListState.events, mapViewModel,log)
                        }
                        is EventViewModel.EventsUIState.Error -> {
                            Text(text = "${eventsListState.error}")
                        }
                    }




                    //create event section
                    CreateEventSection(navController,log)

                    when(garbageSpotsListState){
                        is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
                            log.d{"get garbage spots state -> success"}
                            //garbagespotsnearme section
                            GarbageSpotsNearMe(navController,garbageSpotsListState.garbageSpots,mapViewModel,log)
                        }
                        is GarbageSpotViewModel.GarbageSpotsUIState.Error -> {
                            log.d{"get garbage spots state -> Error"}
                            log.d{"Error -> ${garbageSpotsListState.error}"}
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
private fun GarbageSpotsNearMe(
    navController: NavHostController,
    garbageSpots: List<GarbageSpotDTO>,
    mapViewModel: MapViewModel,
    log: Logger
) {
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
                navController.navigate(Screen.GarbageSpotList.route)
            })

    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        val location by mapViewModel.getLocationLiveData().observeAsState()

        //todo testar com tele
        if(location == null){
            Text("Erro a ler a sua localização")
        }else{
            if(garbageSpots.filter { gs ->
                    locationNearMe(location!!,
                        LatLng(gs.latitude.toDouble(), gs.longitude.toDouble())) <50
                }.isEmpty()) {
                Text("Não existe locais de lixo proximos de si")
            }else{
                LazyHorizontalGrid(
                    modifier = Modifier
                        .height(100.dp),
                    rows = GridCells.Fixed(1),
                    ){
                    garbageSpots.filter { gs ->
                        locationNearMe(location!!,
                            LatLng(gs.latitude.toDouble(), gs.longitude.toDouble())) <50
                    }.forEachIndexed { index, card ->
                        item(span = { GridItemSpan(1) }) {
                            Card(Modifier.clickable {
                                log.d{"Clicked garbage spot -> $card"}
                                log.d{"Navigated to new screen"}
                                navController.navigate(Screen.GarbageSpotInfo.route + "/${card.id}")
                            },
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
    }
}

@Composable
private fun CreateEventSection(
    navController: NavHostController,
    log: Logger
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
                log.d{"Navigated to new screen"}
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
private fun EventsNearMeSection(
    navController: NavHostController,
    events: List<EventDTO>,
    mapViewModel: MapViewModel,
    log: Logger
) {
    Spacer(modifier = Modifier.height(32.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(text = textResource(R.string.lblEventsNearMe))
        ClickableText(text = AnnotatedString(textResource(R.string.lblSeeMoreItems)),
            style = MaterialTheme.typography.body1,
            onClick = {
                log.d{"Navigated to new screen"}
                navController.navigate(Screen.EventList.route)
            })


    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        //todo testar com o telemovel
        val location by mapViewModel.getLocationLiveData().observeAsState()
        if(location == null){
            Text("Erro a ler a sua localização")
        }else{
            if(events.filter { ev -> ev.status == "Criado" &&
                        locationNearMe(location!!,
                            LatLng(ev.latitude.toDouble(), ev.longitude.toDouble()))<50
                }.isEmpty()) {
                Text("Não existe eventos proximos de si")
            }
            else{
                LazyHorizontalGrid(
                    modifier = Modifier
                        .height(100.dp),
                    rows = GridCells.Fixed(1),

                    ){
                    events.filter { ev -> ev.status == "Criado" &&
                            locationNearMe(location!!,
                                LatLng(ev.latitude.toDouble(), ev.longitude.toDouble()))<50
                    }.forEachIndexed { index, card ->
                        item(span = { GridItemSpan(1) }) {
                            Card(
                                Modifier.clickable {
                                    log.d { "Event clicked -> $card" }
                                    log.d { "Navigated to new screen" }
                                    navController.navigate(Screen.EventInfo.route + "/${card.id}")
                                },
                            ) {
                                Column() {
                                    Text(text = card.name)
                                    Text(text = card.startDate)
                                    Text(text = card.status)
                                }


                            }
                        }
                    }
                }
            }
        }
    }
}


private fun locationNearMe(location: LocationDetails, lastLocation:LatLng): Double {
    // Current Location
    var distance = 0.0

    when (location) {
        // If location not null, camera position will be current location
        else -> {
            val lat = location!!.latitude.toDouble()
            val lng = location!!.longitude.toDouble()
            var parseLocationLiveData = LatLng(lat, lng)

            // calculate distance travelled since last location

            distance = calculateDistance(parseLocationLiveData, lastLocation)
        }
    }
    return distance
}