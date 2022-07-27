package com.example.splmobile.android.ui.main.screens.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.splmobile.android.ui.main.screens.MapContent
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.dtos.myInfo.LocationDetails
import com.google.android.gms.maps.model.BitmapDescriptor
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
    println("Ongoing Activity" + location.toString())

    // Default Location, Center of Portugal
    var defaultLocation = LatLng(39.5, -8.0)

    var cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 20f)
    }

    Column {
        GPS(location)
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPosition
        ) {
            // Show Marker in Current Location
            // TODO Change icon appearance
            Marker(
                position = defaultLocation,
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
