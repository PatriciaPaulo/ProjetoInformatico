package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.patternConverter
import com.example.splmobile.android.patternReceiver
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.main.components.iconBoxUI
import com.example.splmobile.android.ui.main.screens.activities.calculateDistance
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.objects.events.EventDTO
import com.example.splmobile.objects.garbageSpots.GarbageSpotDTO
import com.example.splmobile.objects.myInfo.LocationDetails
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import kotlin.math.roundToInt

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
            CommunityAppBar(searchWidgetState, searchTextState, mainViewModel)
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        {
            CommunityUI(eventsListState, navController, mapViewModel, log, garbageSpotsListState)
        },

        )

}

@Composable
private fun CommunityAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    mainViewModel: MainViewModel
) {
    TopAppBar(
        title = {
            Text(
                text = textResource(R.string.lblCommunitySearchBar)
            )
        },
        actions = {

        }
    )

}

@Composable
private fun CommunityUI(
    eventsListState: EventViewModel.EventsUIState,
    navController: NavHostController,
    mapViewModel: MapViewModel,
    log: Logger,
    garbageSpotsListState: GarbageSpotViewModel.GarbageSpotsUIState
) {
    var buttonScreenState = remember { mutableStateOf(R.string.btnCommunity) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        // Apply the padding globally

        if (buttonScreenState.value.equals(R.string.btnCommunity)) {

            //events near me section
            when (eventsListState) {
                is EventViewModel.EventsUIState.Success -> {
                    EventsNearMeSection(navController, eventsListState.events, mapViewModel, log)
                }
                is EventViewModel.EventsUIState.Error -> {
                    Text(text = "${eventsListState.error}")
                }
            }


            //create event section
            CreateEventSection(navController, log)

            when (garbageSpotsListState) {
                is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
                    log.d { "get garbage spots state -> success" }
                    //garbagespotsnearme section
                    GarbageSpotsNearMe(
                        navController,
                        garbageSpotsListState.garbageSpots,
                        mapViewModel,
                        log
                    )
                }
                is GarbageSpotViewModel.GarbageSpotsUIState.Error -> {
                    log.d { "get garbage spots state -> Error" }
                    log.d { "Error -> ${garbageSpotsListState.error}" }
                    Text(text = "${garbageSpotsListState.error}")
                }
            }

        }
        if (buttonScreenState.value.equals(R.string.btnFriends)) {
            Text("friends screen")
        }
    }
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

        Text(text = textResource(R.string.lblGarbageSpotsNearMe),style = MaterialTheme.typography.h6)
        Button(
            onClick = {
                navController.navigate(Screen.GarbageSpotList.route)
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Text(text = textResource(R.string.lblSeeMoreItems))

        }



    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        val location by mapViewModel.getLocationLiveData().observeAsState()
        var parseLocationLiveData = LatLng(0.0, 0.0)
        if (location != null) {
            val lat = location!!.latitude.toDouble()
            val lng = location!!.longitude.toDouble()
            parseLocationLiveData = LatLng(lat, lng)
        }

        if(location == null){
            Text("Erro ao ler a sua localização")
        }else{
            if(garbageSpots.filter { gs ->
                    locationNearMe(location!!,
                        LatLng(gs.latitude.toDouble(), gs.longitude.toDouble())) <50
                }.isEmpty()) {
                Text("Não existem locais de lixo proximos de si")
            }else{
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_spacer)),
                ){
                    garbageSpots.filter { gs ->
                        locationNearMe(location!!,
                            LatLng(gs.latitude.toDouble(), gs.longitude.toDouble())) <50
                    }.forEachIndexed { index, garbagespot ->
                        item {
                            Card (modifier = Modifier
                                .clickable { navController.navigate(Screen.GarbageSpotInfo.route + "/${garbagespot.id}") }
                                .background(color = Color.Transparent)) {
                                iconBoxUI(
                                    modifier = Modifier
                                        .clickable { navController.navigate(Screen.GarbageSpotInfo.route + "/${garbagespot.id}") },
                                    name = garbagespot.name,
                                    distance = if (location != null) (calculateDistance(
                                        parseLocationLiveData,
                                        LatLng(
                                            garbagespot.latitude.toDouble(),
                                            garbagespot.longitude.toDouble()
                                        )
                                    ) * 10.0).roundToInt() / 10.0
                                    else null,
                                    location = null,
                                    details = garbagespot.status,
                                    iconPath = null,
                                )
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(text = textResource(R.string.lblNoEventForMe))
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

        ) {
            Text(text = textResource(R.string.btnCreateEvent))
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
        Text(text = textResource(R.string.lblEventsNearMe), style = MaterialTheme.typography.h6)
        Button(
            onClick = {

                navController.navigate(Screen.EventList.route)
            },
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ) {
            Text(text = textResource(R.string.lblSeeMoreItems))

        }


    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

    ) {
        val location by mapViewModel.getLocationLiveData().observeAsState()
        var parseLocationLiveData = LatLng(0.0, 0.0)
        if (location != null) {
            val lat = location!!.latitude.toDouble()
            val lng = location!!.longitude.toDouble()
            parseLocationLiveData = LatLng(lat, lng)
        }

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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_spacer)),
                ){
                    events.filter { ev -> ev.status == "Criado" &&
                            locationNearMe(location!!,
                                LatLng(ev.latitude.toDouble(), ev.longitude.toDouble()))<50
                    }.forEachIndexed { index, event ->
                        item {
                            Card (modifier = Modifier
                                .clickable { navController.navigate(Screen.EventInfo.route + "/${event.id}") }
                                .background(color = Color.Transparent)
                            ) {
                                iconBoxUI(
                                    modifier = Modifier
                                        .clickable { navController.navigate(Screen.EventInfo.route + "/${event.id}") },
                                    name = event.name,
                                    distance = if (location != null) (calculateDistance(
                                        parseLocationLiveData,
                                        LatLng(
                                            event.latitude.toDouble(),
                                            event.longitude.toDouble()
                                        )
                                    ) * 10.0).roundToInt() / 10.0
                                    else null,
                                    location = null,
                                    details = event.status,
                                    iconPath = null,
                                )
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