package com.example.splmobile.android.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.example.splmobile.objects.myInfo.LocationDetails
import com.google.android.gms.location.*

class LocationLiveData (var context : Context) : LiveData<LocationDetails>() {
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()

        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location.also {
                setLocationData(it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    internal fun startLocationUpdates() {
        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun setLocationData(location : Location?) {
        location?.let {
            location ->
            value = LocationDetails(location.longitude.toString(), location.latitude.toString())
        }
    }

    override fun onInactive() {
        print("onInactive")
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult ?: return

            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }
    // statics in kotlin
    companion object {
        const val MINUTE : Long = 60000
        val locationRequest : LocationRequest = LocationRequest.create().apply {
            // update location every minute
            interval = MINUTE
            // be available to update location every 15s
            fastestInterval = MINUTE/4
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }
}