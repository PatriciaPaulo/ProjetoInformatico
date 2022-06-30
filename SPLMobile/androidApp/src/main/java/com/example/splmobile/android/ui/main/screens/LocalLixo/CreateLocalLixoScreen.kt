package com.example.splmobile.android.ui.main.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.navigation.BottomNavItem
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import com.google.maps.android.compose.Marker


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateLocalLixoScreen(navController: NavHostController, mainViewModel: MainViewModel, localLixoViewModel: LocalLixoViewModel,authViewModel: AuthViewModel,sharedViewModel: SharedViewModel) {
    //default camera position
    var portugal = LatLng(39.5, -8.0)
    var cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(portugal, 7f)

    }
    //android view model states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
                topBar = {
            MapAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                    mainViewModel.updateSearchTextState(newValue = it)
                },
                onCloseClicked = {
                    mainViewModel.updateSearchTextState(newValue = "")
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)

                },
                onSearchClicked = {
                    coroutineScope.launch {
                        Log.d("Map","Searched text, $it")
                        //only change camera position if word written is a place
                        if( !Json.parseToJsonElement(getGeocode(sharedViewModel,it)).jsonObject.containsKey("error") ){
                            cameraPosition.position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    Json.parseToJsonElement(getGeocode(sharedViewModel,it)).jsonObject.get("latt").toString().removePrefix("\"").removeSuffix("\"").toDouble()
                                    ,
                                    Json.parseToJsonElement(getGeocode(sharedViewModel,it)).jsonObject.get("longt").toString().removePrefix("\"").removeSuffix("\"").toDouble()), 10f)

                        }else{
                            Log.d("Map","error, ${Json.parseToJsonElement(getGeocode(sharedViewModel,it)).jsonObject.get("error")}")
                        }

                    }




                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)

                }
            )
        }

    ) {
        var position by remember { mutableStateOf(LatLng(portugal.latitude,portugal.longitude)) }
        var message by remember { mutableStateOf("")}
        val context = LocalContext.current
        BottomSheetScaffold(
            sheetContent = {
                var text by remember { mutableStateOf(TextFieldValue("")) }
                val buttonState = remember { mutableStateOf(false) }

                Text(text = "Nome")

                TextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        if(text.text.isNotEmpty()){
                            if (!position.equals(portugal)) {
                                buttonState.value = true
                            }
                        }
                    }
                )
                Button(
                    onClick = {
                            coroutineScope.launch {
                                message = localLixoViewModel.createLocalLixo(
                                    LocalLixo(
                                        0,
                                        text.text,
                                        0,
                                        String.format("%.4f", position.latitude),
                                        String.format("%.4f", position.longitude),
                                        "Muito Sujo",
                                        false,
                                        "",
                                        ""
                                    ), authViewModel.tokenState.value
                                )
                                when(message){
                                    "" -> print("x == 2")
                                    else -> {
                                        Toast.makeText(
                                            context,
                                            "Showing toast...$message",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                }
                            }
                              },
                    enabled = buttonState.value
                ) {
                    Text("Criar Local de Lixo")
                }


            },
            // Defaults to BottomSheetScaffoldDefaults.SheetPeekHeight
            sheetPeekHeight = 128.dp,
            // Defaults to true
            sheetGesturesEnabled = false

        ) {
            Box(Modifier.fillMaxSize()) {
                var markerPosition = rememberMarkerDragState()
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPosition,
                    onMapClick = {
                        position = it
                    }
                ) {
                    if (!position.equals(portugal)) {
                        Marker(position = position, title = "new lixeira")
                    }

                }
                Button(
                    onClick = {
                        navController.popBackStack()
                    }

                ) {
                    Text(text = "Voltar")
                }
            }
        }


    }

}
/*
*    coroutineScope.launch { // using the `coroutineScope` to `launch` showing the snackbar
                            // taking the `snackbarHostState` from the attached `scaffoldState`
                            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                                message = "Message $message",
                            )
                            when (snackbarResult) {
                                SnackbarResult.Dismissed ->{
                                    Log.d("SnackbarDemo", "Dismissed")
                                    navController.popBackStack()
                                }
                            }
                        }
*
* */