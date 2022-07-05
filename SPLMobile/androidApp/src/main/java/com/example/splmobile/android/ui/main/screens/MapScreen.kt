package com.example.splmobile.android.ui.main.screens
import MapAppBar
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MapScreen(navController: NavHostController,mainViewModel: MainViewModel, localLixoViewModel: LocalLixoViewModel,authViewModel: AuthViewModel, sharedViewModel: SharedViewModel,
              log: Logger
) {


    val drawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    //android view model states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
     //default camera position
    var portugal = LatLng(39.5, -8.0)
    var cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(portugal, 7f)

    }
    var localLixoState = mutableStateOf(
        LocalLixoSer(0,"new",0,"","","Muito sujo",false,"",
            emptyList())
    )
    var createLocalLixoState = mutableStateOf(false)
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
        content =
        { innerPadding ->
            // Apply the padding globally to the whole BottomNavScreensController
            val bottomScaffoldState = rememberBottomSheetScaffoldState()
            val context = LocalContext.current
            BottomSheetScaffold(
                sheetContent = {

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        SheetContent(localLixoState,createLocalLixoState,bottomScaffoldState,localLixoViewModel,navController, authViewModel)
                       /* Button(
                            onClick = {
                                coroutineScope.launch { bottomScaffoldState.bottomSheetState.collapse() }
                            }
                        ) {
                            Text("Click to collapse sheet")
                        }*/
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            if(!localLixoState.value.id.equals(0L)){
                                localLixoState.value =  LocalLixoSer(0,"new",0,"","","",false,"", emptyList())
                            }
                            else{
                                if (localLixoState.value.latitude.isNotEmpty()&&!localLixoState.value.latitude.equals("0.0")) {
                                    if(createLocalLixoState.value){
                                        localLixoViewModel.createLocalLixo(localLixoState.value)
                                    }
                                    else{
                                        Toast.makeText(context,"Dá um nome e um estado ao seu Local", Toast.LENGTH_SHORT)
                                    }
                                }

                                Toast.makeText(context,"Selecione o local", Toast.LENGTH_SHORT)
                            } },
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Localized description")
                    }
                },
                scaffoldState = bottomScaffoldState,
                floatingActionButtonPosition = FabPosition.End,
                sheetPeekHeight = 128.dp,
                /*drawerContent = {
                    Column(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Drawer content")
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = { coroutineScope.launch { bottomScaffoldState.drawerState.close() } }) {
                            Text("Click to close drawer")
                        }
                    }
                }*/
            ) { innerPadding ->
                Scaffold( modifier =
                Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        coroutineScope.launch {
                            if (bottomScaffoldState.bottomSheetState.isCollapsed) {
                                bottomScaffoldState.bottomSheetState.expand()
                            } else {
                                bottomScaffoldState.bottomSheetState.collapse()
                            }
                        }
                    })
                }){
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MapContent(navController,localLixoViewModel.locaisLixoUIState.collectAsState().value,localLixoState)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.Start
                        ) {

                        }
                    }
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
    )
}

 suspend fun getGeocode(mainViewModel:SharedViewModel, nome : String):  String{
    return mainViewModel.getCoordenadas(nome)

}


@Composable
fun MapContent(
    navController: NavHostController,
    locaisLixoState: LocalLixoViewModel.LocaisLixoUIState,
    localLixoState: MutableState<LocalLixoSer>
) {
    var filterLocaisLixo by remember {
        mutableStateOf(emptyList<LocalLixoSer>())
    }
    var portugal = LatLng(39.5, -8.0)


    var newLocalLixoPos by remember { mutableStateOf(LatLng(0.0,0.0)) }
    when(locaisLixoState){
        is LocalLixoViewModel.LocaisLixoUIState.Loading -> CircularProgressIndicator()
        is LocalLixoViewModel.LocaisLixoUIState.Success -> {
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.cardview_dark_background))
                    .wrapContentSize(Alignment.TopStart)
            ) {

            }
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.cardview_dark_background))
                    .wrapContentSize(Alignment.Center)
            ) {

                val locaisLixo = locaisLixoState.locaisLixo
                var portugal = LatLng(39.5, -8.0)
                var cameraPosition = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(portugal, 7f)

                }

                Box(Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPosition,
                        onMapClick = {
                            newLocalLixoPos = it

                        }
                    ) {
                        if (localLixoState.value.id.equals(0L)) {
                            localLixoState.value.longitude = newLocalLixoPos.longitude.toString()
                            localLixoState.value.latitude = newLocalLixoPos.latitude.toString()
                            Marker(position = newLocalLixoPos, title = "new lixeira")
                        }
                        filterLocaisLixo = locaisLixo
                        // filterLocaisLixo.filter { localLixo -> localLixo.aprovado  }
                        locaisLixo.forEach { localLixo ->

                            val lixeiraPosition =
                                LatLng(localLixo.latitude.toDouble(), localLixo.longitude.toDouble())
                            //Log.d("Map", "lixeirrrraa, $localLixo")
                            //Log.d("Map", "pos, $lixeiraPosition")
                            Marker(
                                position = lixeiraPosition,
                                title = localLixo.nome,
                                snippet = localLixo.estado,
                                onClick = {
                                    localLixoState.value = localLixo
                                    true
                                }
                            )

                        }


                    }


                }
            }
        }
        is LocalLixoViewModel.LocaisLixoUIState.Error -> Log.d("screen map", "error")
        is LocalLixoViewModel.LocaisLixoUIState.Offline -> Log.d("screen map", "offline")
    }

    }


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SheetContent(
    localLixo: MutableState<LocalLixoSer>,
    createLocalLixoState: MutableState<Boolean>,
    bottomScaffoldState: BottomSheetScaffoldState,
    localLixoViewModel: LocalLixoViewModel,
    navController: NavHostController,
    authViewModel: AuthViewModel
){
    BoxWithConstraints {
        Log.d("SHEET LOCAL LIXO ID", localLixo.value.id.toString())
        var coroutineScope = rememberCoroutineScope()
        //state that determines if the button is disabled or not
        val updateLocalLixo = remember { mutableStateOf(false) }
        //shown status
        var selectedStatus = remember { mutableStateOf(localLixo.value.estado) }
        //id of the current local lixo
        var selectedId = remember { mutableStateOf(localLixo.value.id) }

        //var message by remember { mutableStateOf("")}
        //apps context
        val context = LocalContext.current

        //variables to create a local lixo
        var nomeLocalLixo by remember { mutableStateOf(TextFieldValue("")) }
        var estadoLocalLixo by remember { mutableStateOf(TextFieldValue("")) }
        val statusList = listOf("Muito sujo", "Pouco sujo", "Limpo")

        //if its a new local lixo
        if(localLixo.value.id.equals(0L)){
            Row {
                Column {
                    Text("Cria um local de lixo!")
                    TextField(
                        value = nomeLocalLixo,
                        label = { Text(text = "Nome Local Lixo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = { newText ->
                            nomeLocalLixo = newText
                        }
                    )
                    var expanded by remember { mutableStateOf(false) }
                    var selectedOptionText by remember { mutableStateOf(statusList[0]) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedOptionText,
                            onValueChange = { },
                            label = { Text("Label") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            statusList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        expanded = false
                                    }
                                ) {
                                    Text(text = selectionOption)
                                }
                            }
                        }
                    }


                    if (nomeLocalLixo.text.isNotEmpty()){
                        localLixo.value.nome = nomeLocalLixo.text
                        localLixo.value.estado = selectedOptionText
                        createLocalLixoState.value = true
                    }else{
                        createLocalLixoState.value = false
                    }
                }
            }
        }else{ // displays info on already existent local lixo
            Box(
                modifier = Modifier.align(Alignment.TopCenter),
                contentAlignment = Alignment.TopCenter
            ) {
                    Column(Modifier.padding(28.dp)) {
                        Text(localLixo.value.nome)
                        Text(localLixo.value.criador.toString())
                    }
                    Spacer(Modifier.height(32.dp))
                    Column{

                        DropDownMenuStatus(
                            selectedStatus,
                            selectedId,
                            localLixo,
                            updateLocalLixo,
                            bottomScaffoldState
                        )
                    }

                }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, bottom = 0.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            localLixoViewModel.updateLocalLixoEstado(localLixo.value,selectedStatus.value,authViewModel.tokenState.value)
                            bottomScaffoldState.bottomSheetState.collapse()
                            /*when(message){
                                "" -> print("x == 2")
                                else -> {
                                    Toast.makeText(
                                        context,
                                        "Showing toast...$message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }*/
                        }

                    },
                    enabled = updateLocalLixo.value,
                ) {
                    Text("Atualizar estado do Local de Lixo")
                }

            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun DropDownMenuStatus(
    selectedStatus: MutableState<String>,
    selectedId: MutableState<Long>,
    localLixo: MutableState<LocalLixoSer>,
    updateLocalLixo: MutableState<Boolean>,
    bottomScaffoldState: BottomSheetScaffoldState
) {
    var coroutineScope = rememberCoroutineScope()
    //state of dropdownmenu
    var expanded by remember { mutableStateOf(false) }
    //available status for locallixo
    val statusList = listOf("Muito sujo", "Pouco sujo", "Limpo")
    //only change selected item if the id has also changed
    if(!localLixo.value.id.equals(selectedId.value)){
        //changes the current locallix
        selectedId.value = localLixo.value.id
        //changes status to be shown
        selectedStatus.value = localLixo.value.estado
        //update button to disabled
        updateLocalLixo.value = false
        //colapses botscaffold
        coroutineScope.launch { bottomScaffoldState.bottomSheetState.collapse() }
    }


    Text(
        text = selectedStatus.value,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { expanded = true })
            .background(
                Color.Gray
            ),
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontSize = 20.sp
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.Magenta
            )

    ) {
        statusList.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                expanded = false
                //check if current localLixo equals the index clicked
                //if different then update the button state
                if (!statusList.indexOf(localLixo.value.estado).equals(index)) {
                    //change status to abled so the button to update status is available
                    updateLocalLixo.value = true
                    //expand to show update button
                    coroutineScope.launch { bottomScaffoldState.bottomSheetState.expand() }

                } else {
                    //if status has not been changed then disable the button
                    updateLocalLixo.value = false
                    //expand to show update button
                    coroutineScope.launch { bottomScaffoldState.bottomSheetState.collapse() }
                }
                //update the dropmenu label to the clicked value
                selectedStatus.value = statusList[index]



            }) {
                Text(textAlign = TextAlign.Center, text = s)
            }
        }
    }
}


