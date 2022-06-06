package com.example.splmobile.android.ui.main.screens.LocalLixo

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.splmobile.android.ui.main.screens.getGeocode
import com.example.splmobile.database.Lixeira
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.lixeiras.LixeiraViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LocalLixoScreen(navController: NavHostController, lixeiraID: Long,lixeiraViewModel: LixeiraViewModel) {
    // shared view model states
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareLixeirasFlow = remember(lixeiraViewModel.lixeirasState, lifecycleOwner) {
        lixeiraViewModel.lixeirasState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val lixeirasState by lifecycleAwareLixeirasFlow.collectAsState(lixeiraViewModel.lixeirasState.value)

    val coroutineScope = rememberCoroutineScope()

    var lixeira by remember { mutableStateOf(Lixeira(0,"aa",0,"1","1","estado",false,"")) }
    LaunchedEffect(Unit) {
        lixeira = lixeiraViewModel.getLixeiraInfo(lixeiraID)!!
    }
    LocalLixoInfo(lixeira,navController)
}


@Composable
fun LocalLixoInfo(lixeira: Lixeira,navController:NavHostController){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cardview_dark_background))
            .wrapContentSize(Alignment.Center)
    ) {

        Text(
            text = "Lixeira #${lixeira.id}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 30.sp
        )
        Text(
            text = "Aprovada?  ${lixeira.aprovado}",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Text(
            text = "Estado: ${lixeira.estado}",
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