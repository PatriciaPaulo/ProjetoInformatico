package com.example.splmobile.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.splmobile.Greeting
import kotlinx.coroutines.MainScope

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
