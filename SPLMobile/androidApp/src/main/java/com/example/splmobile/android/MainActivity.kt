package com.example.splmobile.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.splmobile.android.models.Lixeira
import com.example.splmobile.android.models.UserMap
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.MainScope

private const val TAG = "MainActivity"
const val EXTRA_USER_MAP = "EXTRA_USER_MAP"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_filter)
        val userMaps = generateSampleData()
        val rvMap : RecyclerView = findViewById(R.id.rvMap)
        rvMap.layoutManager = LinearLayoutManager(this)
        rvMap.adapter = MapsAdapter(this, userMaps,object: MapsAdapter.OnClickListener{
            override  fun onItemClick(position: Int){
                Log.i(TAG,"onItemClick $position")
                val intent = Intent(this@MainActivity,ActivityMaps::class.java)
                intent.putExtra(EXTRA_USER_MAP,userMaps[position])
                startActivity(intent)
            }
        })


    }
    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Lixeira("Lixeira 1",37.426, -122.163,"estado1"),
                    Lixeira(  "Lixeira 1",37.430, -122.173, "estado2"),
                    Lixeira("Lixeira 1",37.444, -122.170,"estado3")
                )
            ),
            UserMap("January vacation planning!",
                listOf(
                    Lixeira("Lixeira 1",35.67, 139.65,"estado1"),
                    Lixeira("Lixeira 1",23.34, 85.31,"estado1"),
                    Lixeira( "Lixeira 1",1.35, 103.82,"estado1")
                )),


        )
    }
}
