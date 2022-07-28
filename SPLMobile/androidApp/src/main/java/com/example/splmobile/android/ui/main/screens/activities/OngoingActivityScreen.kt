package com.example.splmobile.android.ui.main.screens.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import com.example.splmobile.android.data.StepSensorData
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.myInfo.LocationDetails
import com.example.splmobile.isTextFieldEmpty
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun OngoingActivity (
    navController: NavController,
    mapViewModel: MapViewModel
) {
    // Current Location
    val location by mapViewModel.getLocationLiveData().observeAsState()

    // Default Location, Center of Portugal
    var defaultLocation = LatLng(39.5, -8.0)
    var pointerLocation = defaultLocation

    var cameraPosition = rememberCameraPositionState {
        if(location == null) {
            position = CameraPosition.fromLatLngZoom(defaultLocation, 16f)
        } else {
            val lat = location!!.latitude.toDouble()
            val lng = location!!.longitude.toDouble()
            println("$lat $lng")
            var parseLocationLiveData = LatLng(lat, lng)

            pointerLocation = parseLocationLiveData
            println("Pointer Location : $parseLocationLiveData" )
            position = CameraPosition.fromLatLngZoom(parseLocationLiveData, 16f)
        }
    }

    var stepCounter by remember { mutableStateOf(0f) }
    step(stepCounter)

    Column {
        Row {
            Text("Current Location:")
            GPS(location)
        }
        Row {
            Text("Steps Counter: $stepCounter")
        }

        // TODO Button add garbage to activity


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
    // TODO Título
    // TODO Cronómetro
    // TODO Resumo Lixo
    // TODO Steps (maybe tirar)
    // TODO Distância so far
    // TODO Add/Edit pictures
    // TODO Adicionar Activity Type
}

// TODO 1/2 da screen ? revise size
@Composable
private fun addGarbageToActivityUI() {
    // TODO Adicionar Garbage Type (Text Field with Suggestions)
    // TODO Adicionar Garbage Type (If can't find match add to db or admin request?)
    // TODO Adicionar Quantidade de Lixo
    // TODO Adicionar Drop Box garbage unit type
}