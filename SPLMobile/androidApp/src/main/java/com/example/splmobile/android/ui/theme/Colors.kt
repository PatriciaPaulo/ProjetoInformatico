package com.example.splmobile.android.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Define color variables
private val Purple200 = Color(0xFFBB86FC)
private val Purple500 = Color(0xFF6200EE)
private val Purple700 = Color(0xFF3700B3)
private val Teal200 = Color(0xFF03DAC5)

// Colors in Keywords for DarkMode
val DarkColorsPalette = darkColors(

    primary = Purple200,
    //primaryVariant =
    secondary = Purple500,
    //secondaryVariant = ,
    background = Color.White,
    surface = Color.LightGray,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,

)

// Colors in Keywords for LightMode
public val LightColorsPalette = lightColors(
    primary = Purple200,
    //primaryVariant =
    secondary = Purple500,
    //secondaryVariant = ,
    background = Color.White,
    surface = Color.LightGray,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
)