package com.example.splmobile.android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Layout Resources
        val btnRegister : Button = findViewById(R.id.btnRegRegister)
        val txtUsername : EditText = findViewById(R.id.txtRegUsername)
        val txtName : EditText = findViewById(R.id.txtRegName)
        val txtEmail : EditText = findViewById(R.id.txtRegEmail)
        val txtPassword : EditText = findViewById(R.id.txtRegPassword)
        val txtPasswordConfirmation : EditText = findViewById(R.id.txtRegPasswordConfirmation)
        val txtRegisterErrors : EditText = findViewById(R.id.txtRegisterErrors)

        fun registerOnClick(){
            /*val mainScope = MainScope()

            val usernameText = txtUsername.text.toString()
            val nameText = txtName.text.toString()
            val emailText = txtEmail.text.toString()
            val passwordText = txtPassword.text.toString()
            val passwordConfirmationText = txtPasswordConfirmation.text.toString()


            val intent = Intent(this, ActivityLogin::class.java)

            if(usernameText.isBlank() || passwordText.isBlank()||nameText.isBlank() || emailText.isBlank() || passwordConfirmationText.isBlank()){
                txtRegisterErrors.setText("Dados incompletos. Preencha todos os campos.")
                //TODO style text to red, boxes to red until filled i think

            } else {

                var rsCode = 0
                var rsBody = ""

                mainScope.launch {
                    kotlin.runCatching {
                        val (responseCode, responseBody) = Authentication().registerRequest(usernameText, nameText,emailText,passwordText,passwordConfirmationText)
                        rsCode = responseCode
                        rsBody = responseBody
                    }.onSuccess {
                        when (rsCode) {
                            200 -> startActivity(intent.apply{ putExtra("token", rsBody) })
                            else -> {
                                //TODO style elements, text to red etc
                                txtRegisterErrors.setText(rsBody)
                            }
                        }
                    }
                }

            }*/

        }

        btnRegister.setOnClickListener{
            registerOnClick()
        }
    }
}