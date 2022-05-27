package com.example.splmobile.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.splmobile.android.models.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "ActivityMaps"

// Implement OnMapReadyCallback.

public class ActivityMap:AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var userMap: UserMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

   }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.i(TAG,"USER MAP RENDER: ${userMap.lixeiras}")
        for (lixeira in userMap.lixeiras){
            //Log.i(TAG,lixeira.toString())
            val latlng = LatLng(lixeira.latitude.toDouble(), lixeira.longitude.toDouble())
            mMap.addMarker(MarkerOptions().position(latlng).title(lixeira.title))

        }

        val portugal = LatLng(39.2, -8.0)
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    portugal.latitude,
                    portugal.longitude
                ), 7.0f
            )
        )
         //mMap.moveCamera(CameraUpdateFactory.newLatLng(portugal))
    }
}