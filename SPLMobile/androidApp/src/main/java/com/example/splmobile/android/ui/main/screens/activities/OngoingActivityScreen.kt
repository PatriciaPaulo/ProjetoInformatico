package com.example.splmobile.android.ui.main.screens.activities

import BackAppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.objects.activities.ActivityTypeSerializable
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.objects.activities.ActivitySerializable
import com.example.splmobile.objects.activities.ExplicitGarbageInActivityDTO
import com.example.splmobile.objects.activities.PatchActivitySerializable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDateTime
import kotlin.math.*

/*
@Composable
fun OngoingActivity(
    navController: NavController,
    mapViewModel: MapViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    activityID: String?,
    garbageSpotViewModel: GarbageSpotViewModel,
    log: Logger
) {
    Button(
        onClick = {
            navController.navigate(BottomNavItem.Home.route)
        }
    ) { Text("Activity") }
}
*/

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OngoingActivity(
    navController: NavController,
    mapViewModel: MapViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    activityID: String?,
    garbageSpotViewModel: GarbageSpotViewModel,
    log: Logger
) {
    val log = log.withTag("OngoingActivity")

    val title = stringResource(R.string.activityTitle)
    Scaffold(
        topBar = { BackAppBar(title, navController) },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            onGoingActivityUI(
                activityID!!.toLong(),
                navController,
                mapViewModel,
                activityViewModel,
                authViewModel,
                garbageSpotViewModel,
                log
            )
        }
    )

}

@Composable
fun onGoingActivityUI(
    activityID: Long,
    navController: NavController,
    mapViewModel: MapViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    log: Logger
) {

    // Current Location
    val location by mapViewModel.getLocationLiveData().observeAsState()
    var lastLocation: LatLng? by remember { mutableStateOf(null) }

    // Default Location, Center of Portugal
    val defaultLocation = LatLng(39.5, -8.0)
    var pointerLocation by remember { mutableStateOf(defaultLocation) }
    val cameraLatLng: CameraPosition

    var distanceTravelled by remember { mutableStateOf(0.0) }
    when (location) {
        // If location is null camera position will be default location
        null -> {
            cameraLatLng = CameraPosition.fromLatLngZoom(defaultLocation, 16f)
        }

        // If location not null, camera position will be current location
        else -> {
            val lat = location!!.latitude.toDouble()
            val lng = location!!.longitude.toDouble()
            var parseLocationLiveData = LatLng(lat, lng)

            // calculate distance travelled since last location
            if (lastLocation != null) {
                distanceTravelled += calculateDistance(parseLocationLiveData, lastLocation)
            }
            lastLocation = parseLocationLiveData

            pointerLocation = parseLocationLiveData
            cameraLatLng = CameraPosition.fromLatLngZoom(parseLocationLiveData, 16f)
        }
    }

    var cameraPosition = rememberCameraPositionState {
        position = cameraLatLng
    }

    var showError by remember { mutableStateOf(false) }
    var showErrorState = { source : Int ->
        if(source == 1){
            showError = true
        } else {
            showError = false
        }
    }

    val shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)
    Column() {
        // Show Map Box

        Box(
            modifier = Modifier
                .weight(2f)
        ) {
            mapActivityUI(cameraPosition, pointerLocation)
        }


        // Information panel
        Box(
            modifier = Modifier
                .weight(3f)
                .clip(shape)
        ) {
            //val shortDistance = round(distanceTravelled * 10.0) / 10.0
            ongoingActivityDataUI(
                activityID,
                //shortDistance,
                distance = 1.0,
                activityViewModel,
                authViewModel,
                navController,
                showError = false,
                showErrorState = { }
            )
        }
    }
}

fun calculateDistance(currentLocation: LatLng, lastLocation: LatLng?): Double {
    val EARTH_RADIUS = 6371 // in km

    // Convert to Radians
    val curLat = currentLocation.latitude / 180 * PI
    val curLng = currentLocation.longitude / 180 * PI
    val lastLat = lastLocation!!.latitude / 180 * PI
    val lastLng = lastLocation!!.longitude / 180 * PI

    // Haversine formula
    val dlng = lastLng - curLng
    val dlat = lastLat - curLat

    val a = sin(dlat / 2).pow(2) + cos(curLat) * cos(lastLat) * sin(dlng / 2).pow(2)
    val c = 2 * asin(sqrt(a))

    return (c * EARTH_RADIUS)
}

@Composable
private fun mapActivityUI(cameraPosition: CameraPositionState, pointerLocation: LatLng) {
    // TODO If not permission, show custom image saying oopsie and on click to call permission requester
    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPosition
    ) {
        // Show Marker in Current Location
        // TODO Change icon appearance
        Marker(
            position = pointerLocation,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ongoingActivityDataUI(
    activityID: Long,
    distance: Double,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    showError: Boolean,
    showErrorState : (Int) -> Unit
) {

    var listGarbageCollected : List<ExplicitGarbageInActivityDTO> = emptyList()
    var garbageKgAmount by remember { mutableStateOf(0f) }
    var garbageLtAmount by remember { mutableStateOf(0f) }
    var garbageUnAmount by remember { mutableStateOf(0f) }


    LaunchedEffect(Unit) {
        // Get Activity Types from DB
        activityViewModel.getActivityTypes()
        activityViewModel.getGarbageInActivity(activityID, authViewModel.tokenState.value) { list ->
            var garbageKgAmount2 = 0f
            var garbageLtAmount2 = 0f
            var garbageUnAmount2 = 0f

            list.forEach { garbage ->
                if (garbage.unit == "kg") {
                    garbageKgAmount2 += garbage.amount
                } else if (garbage.unit == "unidade") {
                    garbageLtAmount2 += garbage.amount
                } else {
                    garbageUnAmount2 += garbage.amount
                }
            }

            garbageKgAmount = garbageKgAmount2
            garbageLtAmount = garbageLtAmount2
            garbageUnAmount = garbageUnAmount2

        }
    }

    var activityTypeState = activityViewModel.activityTypeUIState.collectAsState().value
    var activityTypesList = remember { mutableStateOf(emptyList<ActivityTypeSerializable>()) }
    var actTypeExpanded by remember { mutableStateOf(false) }
    val chooseOptionString = stringResource(R.string.chooseOption)
    var actTypeSelected by remember { mutableStateOf(ActivityTypeSerializable(-1, chooseOptionString, ""))}
    when (activityTypeState) {
        is ActivityViewModel.ActivityTypeUIState.Success -> {
            typeListSuccess(activityTypesList, activityTypeState)
        }
    }

    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.default_margin))
    ) {

        // Escolher Activity Type
        Row() {
            ExposedDropdownMenuBox(
                expanded = actTypeExpanded,
                onExpandedChange = { actTypeExpanded = !actTypeExpanded }
            ) {
                TextField(
                    readOnly = true,
                    value = actTypeSelected.name,
                    onValueChange = { },
                    label = { Text(textResource(R.string.activityType)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = actTypeExpanded
                        )

                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = actTypeExpanded,
                    onDismissRequest = {
                        actTypeExpanded = false
                    }
                ) {
                    activityTypesList.value.forEach { selectedOption ->
                        DropdownMenuItem(
                            onClick = {
                                actTypeSelected = selectedOption
                                actTypeExpanded = false
                                showErrorState(2)
                            }
                        ) {
                            Text(text = selectedOption.name)
                        }
                    }
                }
            }
        }


        if(showError) {
            Text(stringResource(R.string.dataMissing))
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Garbage Collected Resume
            Box(
                modifier = Modifier
                    .weight(1F)
                    .height(dimensionResource(R.dimen.btn_large))
            ) {
                //Number
                Text(
                    text = garbageKgAmount.toString(),
                    fontSize = dimensionResource(R.dimen.txt_large).value.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                //Unit
                Text(
                    text = stringResource(R.string.kg),
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1F)
                    .height(dimensionResource(R.dimen.btn_large))
            ) {
                //Number
                Text(
                    text = garbageLtAmount.toString(),
                    fontSize = dimensionResource(R.dimen.txt_large).value.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                //Unit
                var ltString = stringResource(R.string.lt)
                if (garbageLtAmount != 1f) {
                    ltString += "s"
                }
                Text(
                    text = ltString,
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1F)
                    .height(dimensionResource(R.dimen.btn_large))
            ) {
                //Number
                Text(
                    text = garbageUnAmount.toInt().toString(),
                    fontSize = dimensionResource(R.dimen.txt_large).value.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                //Unit
                var unString = stringResource(R.string.un)
                if (garbageUnAmount != 1f) {
                    unString += "s"
                }
                Text(
                    text = unString,
                    fontSize = dimensionResource(R.dimen.txt_small).value.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1F)
                    .height(dimensionResource(R.dimen.btn_large))
            ) {
                IconButton(
                    modifier = Modifier
                        .fillMaxSize(),
                    onClick = { navController.navigate(Screen.ManageGarbage.route + "/${activityID}") },
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Plus icon",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }


        Button(
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.small_spacer))
                .height(dimensionResource(R.dimen.btn_medium))
                .fillMaxWidth(),
            onClick = {

                if(actTypeSelected.id == -1L) {
                    showErrorState(1)
                } else {
                    // Finish Activity
                    println("${activityID} ${distance} ${actTypeSelected.id} ${LocalDateTime.now()}")
                    activityViewModel.finishActivity(
                        PatchActivitySerializable(
                            id = activityID,
                            distanceTravelled = distance.toString(),
                            activityTypeID = actTypeSelected.id,
                            endDate = LocalDateTime.now().toString()
                        ),
                        authViewModel.tokenState.value,
                    ) {
                        navController.navigate(BottomNavItem.Home.route)
                    }
                }
            },
        ) {
            Text(
                text = stringResource(R.string.btnConcluir)
            )
        }
    }
}

private fun typeListSuccess(
    activityTypesList: MutableState<List<ActivityTypeSerializable>>,
    activityTypeState: ActivityViewModel.ActivityTypeUIState.Success
) {
    activityTypesList.value = activityTypeState.activityTypes
}

