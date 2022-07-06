package com.example.splmobile.android.ui.main.screens
import MapAppBar
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
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
fun MapScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    localLixoViewModel: LocalLixoViewModel,
    authViewModel: AuthViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {
    val bottomScaffoldState = rememberBottomSheetScaffoldState()

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
        LocalLixoSer(0,"new",0,"0.0","0.0","Muito sujo",false,"",
            emptyList())
    )
    var locaisLixoFilterState = mutableStateOf(textResource(R.string.lblFilterLocaisLixoAll).toString())

    var createLocalLixoState = localLixoViewModel.localLixoCreateUIState.collectAsState().value
    var createLocalLixoButtonState = mutableStateOf(false)

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
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->
            // Apply the padding globally
            BottomSheetScaffold(
                scaffoldState = bottomScaffoldState,
                floatingActionButtonPosition = FabPosition.End,
                sheetPeekHeight = 128.dp,

                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            //if not a new local lixo selected
                            if(!localLixoState.value.id.equals(0L)){
                                localLixoState.value =
                                    LocalLixoSer(0,"new",0,"","",
                                        "",false,"", emptyList())
                                coroutineScope.launch { bottomScaffoldState.bottomSheetState.expand() }
                            }
                            else{

                                if(createLocalLixoButtonState.value){
                                    localLixoViewModel.createLocalLixo(localLixoState.value)
                                    coroutineScope.launch { bottomScaffoldState.bottomSheetState.collapse() }

                                }
                                else{

                                }


                            } },
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Localized description")
                    }
                },
                sheetContent = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        SheetContent(localLixoState,
                            createLocalLixoButtonState,
                            bottomScaffoldState,
                            localLixoViewModel,
                            authViewModel)
                    }
                },
                drawerContent = { DrawerCompose(locaisLixoFilterState) },
                drawerShape = customShape()

            ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MapContent(
                            localLixoViewModel.locaisLixoUIState.collectAsState().value,
                            localLixoState,locaisLixoFilterState)

                }
            }
        },

    )
}
@Composable
fun DrawerCompose(locaisLixoFilterState: MutableState<String>) {
    Text(
        text = textResource(R.string.lblFilterLocaisLixoTitle).toString(),
        fontSize = dimensionResource(R.dimen.txt_large).value.sp,
        fontStyle = MaterialTheme.typography.h6.fontStyle
    )

    Log.d("Map","error, DRAWER COMPOSE ${locaisLixoFilterState})}")

    val element1 = textResource(R.string.lblFilterLocaisLixoStatus1).toString()
    val element2 = textResource(R.string.lblFilterLocaisLixoStatus2).toString()
    val element3 = textResource(R.string.lblFilterLocaisLixoStatus3).toString()
    val all = textResource(R.string.lblFilterLocaisLixoAll).toString()
    val mine = textResource(R.string.lblFilterLocaisLixoMine).toString()
    val notAprooved = textResource(R.string.lblFilterLocaisLixoNotAprooved).toString()

    Column(Modifier.padding(16.dp)){
        Button(onClick = {
            locaisLixoFilterState.value = all
            Log.d("Map","error, DRAWER COMPOSEtoodos")
        }, shape = RectangleShape) {
            Text(text = textResource(R.string.lblFilterLocaisLixoAll).toString())
        }
        Button(onClick = {
            locaisLixoFilterState.value = mine
            Log.d("Map","error, DRAWER COMPOSEamine")
        }, shape = RectangleShape) {
            Text(text = textResource(R.string.lblFilterLocaisLixoMine).toString())
        }
        Button(onClick = {
            locaisLixoFilterState.value = notAprooved
            Log.d("Map","error, DRAWER COMPOSEaprooved")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterLocaisLixoNotAprooved).toString())
        }
        Button(onClick = {
            locaisLixoFilterState.value = element1
            Log.d("Map","error, DRAWER COMPOSE1")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterLocaisLixoStatus1).toString())
        }
        Button(onClick = {
            locaisLixoFilterState.value = element2
            Log.d("Map","error, DRAWER COMPOSE2")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterLocaisLixoStatus2).toString())
        }
        Button(onClick = {
            locaisLixoFilterState.value = element3
            Log.d("Map","error, DRAWER COMPOS3d")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterLocaisLixoStatus3).toString())
        }

    }
}
@Composable
fun customShape() = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(left = 0f, top = 0f, right = size.width * 3 / 5, bottom = size.height * 3 / 5))
    }
}

 suspend fun getGeocode(mainViewModel:SharedViewModel, nome : String):  String{
    return mainViewModel.getCoordenadas(nome)

}


@Composable
fun MapContent(
    locaisLixoState: LocalLixoViewModel.LocaisLixoUIState,
    localLixoState: MutableState<LocalLixoSer>,
    locaisLixoFilterState: MutableState<String>
) {
    //todo filter locais
    var filteredLocaisLixo by remember {
        mutableStateOf(emptyList<LocalLixoSer>())
    }

    //todo filter locais

    var newLocalLixoPos by remember { mutableStateOf(LatLng(0.0,0.0)) }

    when(locaisLixoState){
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

                        },
                    ) {
                        if (localLixoState.value.id.equals(0L)) {
                            Log.d("screen map", "-$newLocalLixoPos")
                            localLixoState.value.longitude = newLocalLixoPos.longitude.toString()
                            localLixoState.value.latitude = newLocalLixoPos.latitude.toString()
                            Marker(position = newLocalLixoPos, title = "new lixeira")
                        }else{
                            newLocalLixoPos = LatLng(0.0,0.0)
                        }
                        Log.d("screen map", "FITLER STATE-${locaisLixoFilterState.value}")
                        when(locaisLixoFilterState.value){
                            textResource(R.string.lblFilterLocaisLixoAll).toString() -> {
                                Log.d("screen map", "FITLER STATE- TODAS")
                                filteredLocaisLixo = locaisLixo
                                markerList(filteredLocaisLixo.filter {
                                        localLixo ->
                                    localLixo.aprovado }, localLixoState)
                            }
                            textResource(R.string.lblFilterLocaisLixoMine).toString() ->{
                                //todo my id
                                Log.d("screen map", "FITLER STATE- MINE")
                                markerList( filteredLocaisLixo.filter { localLixo -> localLixo.id == 0L  }, localLixoState)
                            }
                            textResource(R.string.lblFilterLocaisLixoStatus1).toString() -> {
                                Log.d("screen map", "FITLER STATE- MT SUJO")
                                 markerList(
                                     filteredLocaisLixo.filter {
                                             localLixo ->
                                         localLixo.estado == textResource(R.string.lblFilterLocaisLixoStatus1).toString() }
                                     , localLixoState)
                            }
                            textResource(R.string.lblFilterLocaisLixoStatus2).toString() -> {

                                markerList(filteredLocaisLixo.filter { localLixo -> localLixo.estado == textResource(R.string.lblFilterLocaisLixoStatus2).toString() }, localLixoState)
                            }
                            textResource(R.string.lblFilterLocaisLixoStatus3).toString() -> {
                                 markerList( filteredLocaisLixo.filter {
                                         localLixo ->
                                     localLixo.estado == textResource(R.string.lblFilterLocaisLixoStatus3).toString() },
                                     localLixoState)
                            }
                            textResource(R.string.lblFilterLocaisLixoNotAprooved).toString() -> {
                                Log.d("screen map", "FITLER STATE- APROVED")
                                filteredLocaisLixo.filter {
                                        localLixo ->
                                    !localLixo.aprovado }
                                markerList(filteredLocaisLixo, localLixoState)
                            }

                        }


                        // filterLocaisLixo.filter { localLixo -> localLixo.aprovado  }



                    }


                }
            }
        }
        is LocalLixoViewModel.LocaisLixoUIState.Loading -> CircularProgressIndicator()
        is LocalLixoViewModel.LocaisLixoUIState.Error -> Log.d("screen map", "error")
        is LocalLixoViewModel.LocaisLixoUIState.Offline -> Log.d("screen map", "offline")
    }
}

@Composable
private fun markerList(
    filteredLocaisLixo: List<LocalLixoSer>,
    localLixoState: MutableState<LocalLixoSer>
) {

    filteredLocaisLixo.forEach { localLixo ->

        val lixeiraPosition =
            LatLng(localLixo.latitude.toDouble(), localLixo.longitude.toDouble())
        Log.d("Map", "lixeirrrraa, $localLixo")
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


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SheetContent(
    localLixo: MutableState<LocalLixoSer>,
    createLocalLixoButtonState: MutableState<Boolean>,
    bottomScaffoldState: BottomSheetScaffoldState,
    localLixoViewModel: LocalLixoViewModel,
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

        //variables to create a local lixo
        var nomeLocalLixo by remember { mutableStateOf(TextFieldValue("")) }
        var estadoLocalLixo by remember { mutableStateOf(TextFieldValue("")) }
        val statusList = listOf(textResource(R.string.LocalLixoStatusListElement1).toString(), textResource(R.string.LocalLixoStatusListElement2).toString(), textResource(R.string.LocalLixoStatusListElement3).toString())

        //if its a new local lixo
        if(localLixo.value.id.equals(0L)){
            Column {
                Column {
                    Text(
                        textResource(R.string.lblCreateLocalLixo).toString(),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                    //text field for local lixo name
                    TextField(
                        value = nomeLocalLixo,
                        label = { Text(textResource(R.string.lblNomeLocalLixo).toString()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = { newText ->
                            nomeLocalLixo = newText
                        }
                    )
                    //dropdown menu for local lixo status
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
                            label = { Text(textResource(R.string.lblEstadoLocalLixo).toString()) },
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

                    //make createlocallixobutton available and assign the values inserted
                    if ((!(localLixo.value.latitude == "0.0" &&
                                        localLixo.value.longitude == "0.0")) &&
                        nomeLocalLixo.text.isNotEmpty() ){
                        localLixo.value.nome = nomeLocalLixo.text
                        localLixo.value.estado = selectedOptionText

                        createLocalLixoButtonState.value = true
                    }else{
                        //error messages
                        createLocalLixoButtonState.value = false
                        if(localLixo.value.latitude == "0.0" &&
                            localLixo.value.longitude == "0.0"){
                            Text(textResource(R.string.txtPositionError).toString(),
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(
                                    start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                        if (nomeLocalLixo.text.isEmpty()){
                            Text(textResource(R.string.txtLocalLixoError).toString(),
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(
                                    start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }


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
                            //todo show success/error message
                        }

                    },
                    enabled = updateLocalLixo.value,
                ) {
                    Text(textResource(R.string.btnUpdateLocalLixoStatus).toString())
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
    val statusList = listOf(textResource(R.string.LocalLixoStatusListElement1).toString(), textResource(R.string.LocalLixoStatusListElement2).toString(), textResource(R.string.LocalLixoStatusListElement3).toString())

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


