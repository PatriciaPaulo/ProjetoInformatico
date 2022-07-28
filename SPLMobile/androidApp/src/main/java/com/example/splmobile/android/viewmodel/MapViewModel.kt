package com.example.splmobile.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.splmobile.android.data.LocationLiveData
import com.example.splmobile.android.data.StepSensorData

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val locationLiveData = LocationLiveData(application)

    // Get Location Live Data:
    // Returns Coords if it has GPS permission
    // Returns null if it doesn't
    fun getLocationLiveData() = locationLiveData

    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }

}