package com.example.splmobile.android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityLandingPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        //Layout Resources
        val btnLogin : Button = findViewById(R.id.btnLanPagLogin)
        val btnRegister : Button = findViewById(R.id.btnLanPagRegister)


        btnLogin.setOnClickListener {
            startActivity(Intent(this, ActivityLogin::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, ActivityRegister::class.java))
        }
    }
}