package com.example.splmobile.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin : Button = findViewById<Button>(R.id.btnLogin);
        fun loginOnClick(view: View){
            val intent = Intent(this, MainActivity::class.java).apply {  }

            startActivity(intent)
        }
    }
}