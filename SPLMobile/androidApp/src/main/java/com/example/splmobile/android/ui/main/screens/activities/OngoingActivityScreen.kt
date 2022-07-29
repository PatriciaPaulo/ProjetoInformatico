package com.example.splmobile.android.ui.main.screens.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import com.example.splmobile.android.R
import com.example.splmobile.android.data.StepSensorData
import com.example.splmobile.android.data.StopWatch
import com.example.splmobile.android.textResource
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.myInfo.LocationDetails
import com.example.splmobile.isTextFieldEmpty
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.*
import kotlin.math.*

// TODO (if time) -> allow to go to previous screens, save variables states, keep time going, show green bar saying in progress and block creating another activity
// TODO (else) -> disallow going back to other screens

@Composable
fun OngoingActivity (
    navController: NavController,
    mapViewModel: MapViewModel
) {

    ongoingActivityDataUI()

    // Current Location
    val location by mapViewModel.getLocationLiveData().observeAsState()
    var lastLocation : LatLng? = null

    // Default Location, Center of Portugal
    val defaultLocation = LatLng(39.5, -8.0)
    var pointerLocation by remember { mutableStateOf(defaultLocation) }
    val cameraLatLng : CameraPosition

    var distanceTravelled by remember { mutableStateOf(0.0) }
    when (location) {
        null ->{
            print("activity on goin - when in null")
            cameraLatLng = CameraPosition.fromLatLngZoom(defaultLocation, 16f)
        }
        else -> { // Note the block
            print("activity on goin - when in else")
            val lat = location!!.latitude.toDouble()
            val lng = location!!.longitude.toDouble()
            var parseLocationLiveData = LatLng(lat, lng)

            if(lastLocation != null) {
                distanceTravelled += calculateDistance(parseLocationLiveData, lastLocation)
            }
            lastLocation = parseLocationLiveData

            pointerLocation = parseLocationLiveData
            println("Pointer Location : $parseLocationLiveData" )
            cameraLatLng = CameraPosition.fromLatLngZoom(parseLocationLiveData, 16f)
        }
    }



    var cameraPosition = rememberCameraPositionState {
        position = cameraLatLng
    }

    var stepCounter by remember { mutableStateOf(0f) }
    step(stepCounter)

    Column {
        Row {
            Text("Current Location:")
            GPS(location)
        }

        Text("Steps Counter: $stepCounter")
        Text("KM: $distanceTravelled")

        // TODO Button add garbage to activity


        // TODO If not permission, show custom image saying oopsie and on click to call permission requester
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPosition
        ) {
            // Show Marker in Current Location
            // TODO Change icon appearance
            // TODO Marker is broken (places default location always)
            Marker(
                position = pointerLocation,
            )
        }
    }

}

fun calculateDistance(currentLocation: LatLng, lastLocation: LatLng) : Double {
    val EARTH_RADIUS = 6371 // in km

    // Convert to Radians
    val curLat = currentLocation.latitude / 180 * PI
    val curLng = currentLocation.longitude / 180 * PI
    val lastLat = lastLocation.latitude / 180 * PI
    val lastLng = lastLocation.longitude / 180 * PI

    // Haversine formula
    val dlng = lastLng - curLng
    val dlat = lastLat - curLat

    val a = sin(dlat/2).pow(2) + cos(curLat) * cos(lastLat) * sin(dlng / 2).pow(2)
    val c = 2 * asin(sqrt(a))
    Log.d("ongoing activityu", (c * EARTH_RADIUS).toString())


    return (c * EARTH_RADIUS)
}

@Composable
private fun GPS(currentLocation : LocationDetails?) {
    currentLocation?.let {
        Text(text = currentLocation.latitude)
        Text(text = currentLocation.longitude)
    }
}

@Composable
private fun step(stepCounter : Float) {
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

// TODO 1/2 da screen
@Composable
private fun ongoingActivityDataUI() {
    val defaultTitle = stringResource(R.string.activityTitle)
    var activityName by remember { mutableStateOf(defaultTitle) }
    var activityNameUpdate by remember { mutableStateOf(defaultTitle) }
    // Status returns true if name is being edited
    var activityNameStatus by remember { mutableStateOf(false) }


    Column() {
        // Activity Name, Editable Buttons
        Row() {
            if(activityNameStatus){
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

            if(!activityNameStatus){
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
        runStopWatch()


        Row() {
            // TODO Resumo Lixo
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                Button(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.btn_large))
                        .align(Alignment.Center),
                    onClick = { }
                ) { }
            }
            // TODO Steps (maybe tirar)

            // TODO Distância so far
            Box(
                modifier = Modifier
                    .weight(1F)
            ) {
                Button(
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.btn_large))
                        .align(Alignment.Center),
                    onClick = { }
                ) { }
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

        // TODO Adicionar Activity Type
        // TODO Concluir BTN
        // TODO Cancelar BTN
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
// TODO 1/2 da screen ? revise size
@Composable
private fun addGarbageToActivityUI() {
    // TODO Adicionar Garbage Type (Text Field with Suggestions)
    // TODO Adicionar Garbage Type (If can't find match add to db or admin request?)
    // TODO Adicionar Quantidade de Lixo
    // TODO Adicionar Drop Box garbage unit type
}