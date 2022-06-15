package com.example.splmobile.android.ui.main.screens.LocalLixo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import com.example.splmobile.android.R
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LocalLixoScreen(navController: NavHostController, lixeiraID: Long,localLixoViewModel: LocalLixoViewModel) {
    var lixeira by remember { mutableStateOf(LocalLixo(0,"aa","user","1","1","estado",false,"")) }
    LaunchedEffect(Unit) {
        lixeira = localLixoViewModel.getLocalLixoInfo(lixeiraID)!!
    }
    LocalLixoInfo(lixeira,navController)
}


@Composable
fun LocalLixoInfo(localLixo: LocalLixo,navController:NavHostController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cardview_dark_background))
            .wrapContentSize(Alignment.Center)
    ) {

        Text(
            text = "localLixo #${localLixo.id}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )
        Text(
            text = "Aprovada?  ${localLixo.aprovado}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Text(
            text = "Estado: ${localLixo.estado}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

    }
    Button(

         onClick = {
            navController.popBackStack()
        }

    ) {
        Text(text = "Voltar")
    }
}