package com.example.splmobile.android.ui.main.screens
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.components.SearchBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.main.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.database.Lixeira
import com.example.splmobile.ktor.lixeiras.LixeiraApi
import com.example.splmobile.ktor.other.requests
import com.example.splmobile.ktor.other.requestsAPI
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.lixeiras.LixeiraViewModel
import com.example.splmobile.models.lixeiras.LixeiraViewState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.SelectClause0
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MapScreen(navController: NavHostController,mainViewModel: MainViewModel, lixeiraViewModel: LixeiraViewModel, sharedViewModel: SharedViewModel,
              log: Logger
) {

    // shared view model states
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareLixeirasFlow = remember(lixeiraViewModel.lixeirasState, lifecycleOwner) {
        lixeiraViewModel.lixeirasState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val lixeirasState by lifecycleAwareLixeirasFlow.collectAsState(lixeiraViewModel.lixeirasState.value)
    //android view model states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState

    // map settings
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
    }

    val coroutineScope = rememberCoroutineScope()

    //default camera position
    var portugal = LatLng(39.5, -8.0)
    var cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(portugal, 7f)

    }

    Scaffold(
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
                                LatLng(Json.parseToJsonElement(getGeocode(sharedViewModel,it)).jsonObject.get("latt").toString().removePrefix("\"").removeSuffix("\"").toDouble()
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
        },
        content ={
            MapContent(navController,lixeirasState,cameraPosition)
        }

        )


}
 suspend fun getGeocode(mainViewModel:SharedViewModel, nome : String):  String{

    return mainViewModel.getCoordenadas(nome)

}


@Composable
fun MapContent(navController: NavHostController, lixeirasState: LixeiraViewState, cameraPosition: CameraPositionState) {
    Box(
        modifier = Modifier
            .background(colorResource(id = R.color.cardview_dark_background))
            .wrapContentSize(Alignment.Center)
    ){
        val lixeiras = lixeirasState.lixeiras
        if (lixeiras != null) {

            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPosition
                ) {
                    lixeiras.forEach { lixeira ->

                        val lixeiraPosition =
                            LatLng(lixeira.latitude.toDouble(), lixeira.longitude.toDouble())

                        Marker(
                            position = lixeiraPosition,
                            title = lixeira.nome,
                            snippet = lixeira.estado,
                            onClick = {
                                //navController.currentBackStackEntry?.arguments?.putLong("lixeiraID",lixeira.id)
                                navController.navigate(Screen.LocalLixo.route+"/${lixeira.id}")

                                true
                            }
                        )

                    }

                }
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Button(
                        onClick = { /* ... */ },
                        // Uses ButtonDefaults.ContentPadding by default
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            top = 12.dp,
                            end = 20.dp,
                            bottom = 12.dp
                        ),

                        ) {
                        // Inner content including an icon and a text label

                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("")
                    }
                }

            }
        }
    }

}


@Composable
fun DefaultAppBar(onSearchClicked: ()-> Unit){
    TopAppBar(
        title = {
            Text(
                text = "Mapa"
            )
        },
        actions = {
            IconButton(
                onClick = { onSearchClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Procurar icon",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun MapAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: ()-> Unit,
    onSearchClicked:(String) ->Unit,
    onSearchTriggered: () -> Unit
){
    when(searchWidgetState){
        SearchWidgetState.CLOSED -> {
            DefaultAppBar (
                onSearchClicked = onSearchTriggered
            )
        }
        SearchWidgetState.OPENED->{
            SearchBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}


