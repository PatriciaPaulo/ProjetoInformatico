package com.example.splmobile.android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.splmobile.OLDSTUFF.apiAccesses.Authentication
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ActivityLogin : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Layout Resources
        val btnLogin : Button = findViewById(R.id.btnLogLogin)
        val btnRegister : TextView = findViewById(R.id.txtLogRegister)
        val txtUsername : EditText = findViewById(R.id.txtLogUsername)
        val txtPassword : EditText = findViewById(R.id.txtLogPassword)
        val lblLoginErrors : EditText = findViewById(R.id.lblLogLoginErrors)
        lblLoginErrors.setEnabled(false)

        fun loginOnClick(){
            val mainScope = MainScope()

            val usernameText = txtUsername.text.toString()
            val passwordText = txtPassword.text.toString()

            val intent = Intent(this, ActivityMap::class.java)

            if(usernameText.isBlank() || passwordText.isBlank()){
                lblLoginErrors.setText("Dados incompletos. Preencha todos os campos.")
                //TODO style text to red, boxes to red until filled i think
                //Todo change hardcode to android resources
            } else {
                /*TODO Loading while wait
                   val tv: TextView = findViewById(R.id.text_view)
                   tv.text = "Loading..."
                   */

                var rsCode = 0
                var rsBody = ""

                mainScope.launch {
                   /* kotlin.runCatching {
                        val (responseCode, responseBody) = Authentication().loginRequest(usernameText, passwordText)
                        rsCode = responseCode
                        rsBody = responseBody
                    }.onSuccess {
                        when (rsCode) {
                            200 -> startActivity(intent.apply{ putExtra("token", rsBody) })
                            else -> {
                                //TODO style elements, text to red etc
                                lblLoginErrors.setText(rsBody)
                            }
                        }
                    }*/
                }

            }

        }

        btnLogin.setOnClickListener{
            loginOnClick()
        }

        btnRegister.setOnClickListener{
            startActivity(Intent(this, ActivityRegister::class.java))
        }

    }
}