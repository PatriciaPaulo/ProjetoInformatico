package com.example.splmobile.android.ui.main.screens.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.data.StepSensorData
import com.example.splmobile.android.data.StopWatch
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.activities.ActivityTypeSerializable
import com.example.splmobile.dtos.activities.CreateActivitySerializable
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.serialization.builtins.serializer
import kotlin.math.*

// TODO (if time) -> allow to go to previous screens, save variables states, keep time going, show green bar saying in progress and block creating another activity
// TODO (else) -> disallow going back to other screens

@Composable
fun OngoingActivity(
    navController: NavController,
    mapViewModel: MapViewModel,
    activityViewModel: ActivityViewModel,
    authViewModel: AuthViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    log: Logger
) {
    LaunchedEffect(Unit) {


    }

    var createActivityState = activityViewModel.activityCreateUIState.collectAsState().value



    onGoingActivityUI(navController, mapViewModel, activityViewModel, authViewModel, garbageSpotViewModel, log)

}


@Composable
fun onGoingActivityUI(
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

    var stepCounter by remember { mutableStateOf(0f) }
    step(stepCounter)

    val shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)
    Column() {
        // Show Map Box
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            mapActivityUI(cameraPosition, pointerLocation)
        }

        // Information panel
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(shape)
        ) {
            val shortDistance = round(distanceTravelled * 10.0) / 10.0
            ongoingActivityDataUI(shortDistance, activityViewModel, navController)
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
private fun step(stepCounter: Float) {
    println("-------------- STEP COUNT DEBUG ------------")
    // Get Current Context
    val context = LocalContext.current

    // Set up the sensor manager
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // Set up sensor to get steps
    val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


    val stepSensorData = StepSensorData(stepCounter)

    stepSensor?.let {
        sensorManager.registerListener(stepSensorData, it, SensorManager.SENSOR_DELAY_UI)
    }
}

@Composable
private fun runStopWatch() {
    val stopWatch = remember { StopWatch() }

    stopWatch.startStopwatch()

    Text(
        text = stopWatch.formattedTime
    )
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
    distance: Double,
    activityViewModel: ActivityViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        // Get Activity Types from DB
        activityViewModel.getActivityTypes()
    }

    val defaultTitle = stringResource(R.string.activityTitle)
    var activityName by remember { mutableStateOf(defaultTitle) }
    var activityNameUpdate by remember { mutableStateOf(defaultTitle) }
    // Status returns true if name is being edited
    var activityNameStatus by remember { mutableStateOf(false) }

    var activityTypeState = activityViewModel.activityTypeUIState.collectAsState().value
    var activityTypesList = remember { mutableStateOf(emptyList<ActivityTypeSerializable>()) }
    var actTypeExpanded by remember { mutableStateOf(false) }

    var showGarbageTypeManager by remember { mutableStateOf(false) }

    when (activityTypeState) {
        is ActivityViewModel.ActivityTypeUIState.Success -> {
            activityTypesList.value = activityTypeState.activityTypes
        }
    }

    // TODO Change to res
    var actTypeSelected by remember { mutableStateOf("Escolha uma opção") }


    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.default_margin))
    ) {
        // Activity Name, Edit Name Buttons
        Row() {
            if (activityNameStatus) {
                // Editable Activity Name
                OutlinedTextField(
                    value = activityName,
                    onValueChange = { activityName = it },
                    textStyle = TextStyle(fontSize = dimensionResource(R.dimen.small_title).value.sp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                )
            } else {
                // Non-editable Activity Name
                Text(
                    text = activityName,
                    fontSize = dimensionResource(R.dimen.small_title).value.sp,
                )
            }

            if (!activityNameStatus) {
                // Edit Name Button
                Button(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.btn_small)),
                    onClick = {
                        activityNameStatus = true
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.ic_main_chat),
                        contentDescription = null
                    )
                }

            } else {
                Row() {
                    // Save Name Button
                    Button(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.btn_small)),
                        onClick = {
                            activityNameUpdate = activityName
                            activityNameStatus = false
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_main_profile),
                            contentDescription = null
                        )
                    }

                    // Cancel Button
                    Button(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.btn_small)),
                        onClick = {
                            activityName = activityNameUpdate
                            activityNameStatus = false
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_main_home),
                            contentDescription = null
                        )
                    }
                }
            }

            // TODO Have to check if mutable state of Activity Name remains if I change page (thinks nots)
        }


        // Chronometer
        // TODO On app close keep counting
        runStopWatch()

        // Escolher Activity Type
        Row() {
            ExposedDropdownMenuBox(
                expanded = actTypeExpanded,
                onExpandedChange = { actTypeExpanded = !actTypeExpanded }
            ) {
                TextField(
                    readOnly = true,
                    value = actTypeSelected,
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
                                actTypeSelected = selectedOption.name
                                actTypeExpanded = false
                            }
                        ) {
                            Text(text = selectedOption.name)
                        }
                    }
                }
            }
        }

        Row() {
            // TODO Resumo Lixo
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                Text("8 Kg") // TODO Change to get lixo from activity and roughly calculate how much lixo there is
                Button(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.btn_large))
                        .align(Alignment.Center),
                    onClick = { navController.navigate(Screen.ManageGarbage.route) }
                ) {
                    Text("+") // TODO Change to ICON
                }
            }

            // Travelled Distance
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = distance.toString(),
                        fontSize = dimensionResource(R.dimen.title).value.sp
                    )
                    Text(textResource(R.string.km))
                }
            }

            // TODO Add/Edit pictures
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.75f)
                        .align(Alignment.Center)
                        .background(color = Color.Blue),
                ) { }
            }
        }
    }
    // TODO Concluir BTN
    // TODO Cancelar BTN
    // TODO Backtrace button fazer o mesmo que cancelar action

}
