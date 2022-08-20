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
import androidx.compose.ui.res.stringResource
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
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun MapScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {
    val log = log.withTag("MapScreen")


    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState

    LaunchedEffect(Unit) {
        log.d {"Get garbage spots launched"}

        garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)


    }


    Scaffold(
        topBar = {

            MapAppBar(
                title = textResource(R.string.lblMapSearchBar).toString(),
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
                        log.d {"Searched text, $it "}
                        //only change camera position if word written is a place

                        sharedViewModel.getCoordenadas(it)


                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)

                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->
            val bottomScaffoldState = rememberBottomSheetScaffoldState()
            // Apply the padding globally
            ScafoldContentSection(
                userInfoViewModel,
                garbageSpotViewModel,
                log,
                authViewModel,
                sharedViewModel,
                bottomScaffoldState
            )
        },

    )
}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
private fun ScafoldContentSection(
    userInfoViewModel: UserInfoViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    log: Logger,
    authViewModel: AuthViewModel,
    sharedViewModel: SharedViewModel,
    bottomScaffoldState: BottomSheetScaffoldState,

) {
    var coordendasState = sharedViewModel.coordenatesUIState.collectAsState().value
    var emptyGarbageSpot = GarbageSpotDTO(
        0, "new", userInfoViewModel.myIdUIState.value, "0.0", "0.0", "Muito sujo", false,
        "", emptyList()
    )

    //default variables
    var garbageSpotState = mutableStateOf(emptyGarbageSpot)
    var garbageSpotsFilterState =
        mutableStateOf(textResource(R.string.lblFilterGarbageSpotsAll).toString())
    var createGarbageSpotButtonState = mutableStateOf(false)

    //variables to create a local lixo
    var nomeGarbageSpotState = remember { mutableStateOf(TextFieldValue("")) }
    var newGarbageSpotPos = remember { mutableStateOf(LatLng(0.0, 0.0)) }






    garbageSpotsStateSection(
        garbageSpotViewModel,
        log,
        authViewModel,
        createGarbageSpotButtonState,
        garbageSpotState,
        emptyGarbageSpot,
        nomeGarbageSpotState,
        newGarbageSpotPos
    )


    BottomSheetScaffold(
        scaffoldState = bottomScaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        sheetPeekHeight = 128.dp,

        floatingActionButton = {
            var coroutineScope = rememberCoroutineScope()
            FloatingActionButton(
                onClick = {

                    //if not a new local lixo selected

                    if (!garbageSpotState.value.id.equals(0L)) {
                        log.d { " Garbage Spot selected " }
                        garbageSpotState.value = emptyGarbageSpot
                        coroutineScope.launch { bottomScaffoldState.bottomSheetState.expand() }
                    } else {
                        log.d { " new Garbage Spot " }
                        coroutineScope.launch { bottomScaffoldState.bottomSheetState.expand() }
                        if (createGarbageSpotButtonState.value) {
                            log.d { " Create Garbage Spot request sent " }
                            garbageSpotViewModel.createGarbageSpot(
                                garbageSpotState.value,
                                authViewModel.tokenState.value
                            )
                            coroutineScope.launch { bottomScaffoldState.bottomSheetState.collapse() }


                        }


                    }


                }
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
                SheetContent(
                    garbageSpotState,
                    createGarbageSpotButtonState,
                    nomeGarbageSpotState,
                    bottomScaffoldState,
                    garbageSpotViewModel,
                    authViewModel, log
                )
            }
        },
        drawerContent = { DrawerFilterCompose(garbageSpotsFilterState) },

        drawerShape = customDrawerShape()

    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            log.d { "In MapContent " }
            ScreenContent(
                garbageSpotViewModel.garbageSpotsUIState.collectAsState().value,
                garbageSpotState,
                newGarbageSpotPos,
                garbageSpotsFilterState,
                userInfoViewModel,
                sharedViewModel,
                log
            )

        }
    }
}

@Composable
private fun CoordenatesStateSection(
    coordendasState: SharedViewModel.CoordenatesUIState,
    positionState: CameraPositionState,
) {

    when (coordendasState) {
        is SharedViewModel.CoordenatesUIState.Success -> {
            Log.d("map","coordenatesuistate success ${coordendasState.latitude}")
            positionState.position = CameraPosition.fromLatLngZoom(
                LatLng(
                    coordendasState.latitude,
                    coordendasState.longitude
                ), 10f
            )

        }

    }
}

@Composable
private fun garbageSpotsStateSection(
    garbageSpotViewModel: GarbageSpotViewModel,
    log: Logger,
    authViewModel: AuthViewModel,
    createGarbageSpotButtonState: MutableState<Boolean>,
    garbageSpotState: MutableState<GarbageSpotDTO>,
    emptyGarbageSpot: GarbageSpotDTO,
    nomeGarbageSpotState: MutableState<TextFieldValue>,
    newGarbageSpotPos: MutableState<LatLng>
) {
    var garbageSpotsState = garbageSpotViewModel.garbageSpotCreateUIState.collectAsState().value
    when (garbageSpotsState) {
        is GarbageSpotViewModel.GarbageSpotCreateUIState.Success -> {
            log.d { "Get garbage spots state -> Success " }
            //update list
            garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
            //reset create local lixo
            createGarbageSpotButtonState.value = false
            garbageSpotState.value = emptyGarbageSpot
            nomeGarbageSpotState.value = TextFieldValue("")
            newGarbageSpotPos.value = LatLng(0.0, 0.0)

            Text(
                text = stringResource(R.string.lblCreateGarbageSpotSuccess),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
            )

        }
        is GarbageSpotViewModel.GarbageSpotCreateUIState.Error -> {
            log.d { "Get garbage spots state -> Error " }
            Text(
                text = garbageSpotsState.error,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
            )

        }
    }
}

@Composable
fun DrawerFilterCompose(
    garbageSpotsFilterState: MutableState<String>
) {
    Text(
        text = textResource(R.string.lblFilterGarbageSpotsTitle).toString(),
        fontSize = dimensionResource(R.dimen.txt_large).value.sp,
        fontStyle = MaterialTheme.typography.h6.fontStyle
    )

    Log.d("Map","error, DRAWER COMPOSE ${garbageSpotsFilterState})}")

    val element1 = textResource(R.string.lblFilterGarbageSpotsStatus1).toString()
    val element2 = textResource(R.string.lblFilterGarbageSpotsStatus2).toString()
    val element3 = textResource(R.string.lblFilterGarbageSpotsStatus3).toString()
    val all = textResource(R.string.lblFilterGarbageSpotsAll).toString()
    val mine = textResource(R.string.lblFilterGarbageSpotsMine).toString()


    Column(Modifier.padding(16.dp)){
        Button(onClick = {
            garbageSpotsFilterState.value = all
            Log.d("Map","error, DRAWER COMPOSEtoodos")
        }, shape = RectangleShape) {
            Text(text = textResource(R.string.lblFilterGarbageSpotsAll).toString())
        }
        Button(onClick = {
            garbageSpotsFilterState.value = mine
            Log.d("Map","error, DRAWER COMPOSEamine")
        }, shape = RectangleShape) {
            Text(text = textResource(R.string.lblFilterGarbageSpotsMine).toString())
        }
        Button(onClick = {
            garbageSpotsFilterState.value = element1
            Log.d("Map","error, DRAWER COMPOSE1")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterGarbageSpotsStatus1).toString())
        }
        Button(onClick = {
            garbageSpotsFilterState.value = element2
            Log.d("Map","error, DRAWER COMPOSE2")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterGarbageSpotsStatus2).toString())
        }
        Button(onClick = {
            garbageSpotsFilterState.value = element3
            Log.d("Map","error, DRAWER COMPOS3d")
        }, shape = RectangleShape) {
            Text(text =  textResource(R.string.lblFilterGarbageSpotsStatus3).toString())
        }

    }
}

@Composable
fun customDrawerShape() = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(left = 0f, top = 0f, right = size.width * 3 / 5, bottom = size.height * 3 / 5))
    }
}



@Composable
fun ScreenContent(
    garbageSpotsState: GarbageSpotViewModel.GarbageSpotsUIState,
    garbageSpotState: MutableState<GarbageSpotDTO>,
    newGarbageSpotPos: MutableState<LatLng>,
    garbageSpotsFilterState: MutableState<String>,
    userInfoViewModel: UserInfoViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {


    when(garbageSpotsState){
        is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
            log.d {" All garbage spots state -> Success "}
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

                val garbageSpots = garbageSpotsState.garbageSpots

                MapContent(
                    newGarbageSpotPos,
                    garbageSpotState,
                    log,
                    garbageSpots,
                    garbageSpotsFilterState,
                    userInfoViewModel,
                    sharedViewModel
                )
            }
        }
        is GarbageSpotViewModel.GarbageSpotsUIState.Loading -> {
            log.d {"Get all garbage spots state -> Loading"}
            CircularProgressIndicator()
        }
        is GarbageSpotViewModel.GarbageSpotsUIState.Error -> {
            log.d {"Get all garbage spots state -> Error"}
            log.d {"Error -> ${garbageSpotsState.error}"}
        }
    }
}

@Composable
private fun MapContent(
    newGarbageSpotPos: MutableState<LatLng>,
    garbageSpotState: MutableState<GarbageSpotDTO>,
    log: Logger,
    garbageSpots: List<GarbageSpotDTO>,
    garbageSpotsFilterState: MutableState<String>,
    userInfoViewModel: UserInfoViewModel,
    sharedViewModel: SharedViewModel
) {
    var filteredGarbageSpots by remember {
        mutableStateOf(emptyList<GarbageSpotDTO>())
    }
    var coordendasState = sharedViewModel.coordenatesUIState.collectAsState().value
    var portugal = LatLng(39.5, -8.0)
    var cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(portugal, 7f)

    }
    CoordenatesStateSection(coordendasState,cameraPosition)
    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPosition,
            onMapClick = {
                newGarbageSpotPos.value = it

            },
        ) {

            if (garbageSpotState.value.id.equals(0L)) {

                log.d { "new garbage spot position $newGarbageSpotPos" }

                garbageSpotState.value.longitude = newGarbageSpotPos.value.longitude.toString()
                garbageSpotState.value.latitude = newGarbageSpotPos.value.latitude.toString()
                Marker(position = newGarbageSpotPos.value, title = "new lixeira")
            } else {
                log.d { "no position selected" }
                newGarbageSpotPos.value = LatLng(0.0, 0.0)
            }

            filteredGarbageSpots = garbageSpots.filter { garbageSpot ->
                garbageSpot.approved
            }
            log.d { "Filter state value -> ${garbageSpotsFilterState.value}" }

            when (garbageSpotsFilterState.value) {
                textResource(R.string.lblFilterGarbageSpotsAll) -> {
                    log.d { "Filter state -> ${textResource(R.string.lblFilterGarbageSpotsAll)}" }
                    markerFilterList(filteredGarbageSpots, garbageSpotState, log)
                }
                textResource(R.string.lblFilterGarbageSpotsMine) -> {
                    log.d { "Filter state -> ${textResource(R.string.lblFilterGarbageSpotsMine)}" }
                    log.d { "My id -> ${userInfoViewModel.myIdUIState.value}" }

                    markerFilterList(
                        garbageSpots.filter { garbageSpot ->
                            garbageSpot.creator == userInfoViewModel.myIdUIState.value
                        },
                        garbageSpotState,
                        log
                    )
                }
                textResource(R.string.lblFilterGarbageSpotsStatus1) -> {
                    log.d { "Filter state -> ${textResource(R.string.lblFilterGarbageSpotsStatus1)}" }
                    markerFilterList(
                        filteredGarbageSpots.filter { garbageSpot ->
                            garbageSpot.status == textResource(R.string.lblFilterGarbageSpotsStatus1).toString()
                        }, garbageSpotState, log
                    )
                }
                textResource(R.string.lblFilterGarbageSpotsStatus2) -> {
                    log.d { "Filter state -> ${textResource(R.string.lblFilterGarbageSpotsStatus2)}" }
                    markerFilterList(
                        filteredGarbageSpots.filter { garbageSpot ->
                            garbageSpot.status == textResource(
                                R.string.lblFilterGarbageSpotsStatus2
                            ).toString()
                        },
                        garbageSpotState,
                        log
                    )
                }
                textResource(R.string.lblFilterGarbageSpotsStatus3) -> {
                    log.d { "Filter state -> ${textResource(R.string.lblFilterGarbageSpotsStatus3)}" }
                    markerFilterList(
                        filteredGarbageSpots.filter { garbageSpot ->
                            garbageSpot.status == textResource(R.string.lblFilterGarbageSpotsStatus3).toString()
                        },
                        garbageSpotState,
                        log
                    )
                }

            }
        }
    }
}

@Composable
private fun markerFilterList(
    filteredGarbageSpots: List<GarbageSpotDTO>,
    garbageSpotState: MutableState<GarbageSpotDTO>,
    log: Logger
) {
    log.d {"Marker filtering list"}
    filteredGarbageSpots.forEach { garbageSpot ->

        val lixeiraPosition = LatLng(garbageSpot.latitude.toDouble(), garbageSpot.longitude.toDouble())
        //Log.d("Map", "lixeirrrraa, $garbageSpot")
        //Log.d("Map", "pos, $lixeiraPosition")
        //todo mudar o icon baseado no estado
        Marker(
            position = lixeiraPosition,
            title = garbageSpot.name,
            snippet = garbageSpot.status,
            onClick = {
                //selects garbage spot to read info
                garbageSpotState.value = garbageSpot
                true
            }
        )

    }
}


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SheetContent(
    garbageSpot: MutableState<GarbageSpotDTO>,
    createGarbageSpotButtonState: MutableState<Boolean>,
    nomeGarbageSpotState: MutableState<TextFieldValue>,
    bottomScaffoldState: BottomSheetScaffoldState,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    log: Logger
){
    BoxWithConstraints {

        var coroutineScope = rememberCoroutineScope()
        //state that determines if the button is disabled or not
        val updateGarbageSpot = remember { mutableStateOf(false) }
        //shown status
        var selectedStatus = remember { mutableStateOf(garbageSpot.value.status) }
        //id of the current local lixo
        var selectedId = remember { mutableStateOf(garbageSpot.value.id) }

        var createGarbageSpotState = garbageSpotViewModel.garbageSpotCreateUIState.collectAsState().value
        var updateGarbageSpotState = garbageSpotViewModel.garbageSpotUpdateUIState.collectAsState().value
        val statusList = listOf(textResource(R.string.GarbageSpotStatusListElement1).toString(), textResource(R.string.GarbageSpotStatusListElement2).toString(), textResource(R.string.GarbageSpotStatusListElement3).toString())





        //if its a new local lixo
        if(garbageSpot.value.id.equals(0L)){
            log.d {"New garbage spot"}
            Column {
                Column {
                    when(createGarbageSpotState){
                        is GarbageSpotViewModel.GarbageSpotCreateUIState.Success -> {
                            log.d {"Create Garbage Spot state -> Success"}
                            Text(
                                text = textResource(R.string.txtGarbageSpotCreateSuccess).toString(),
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                            )
                            garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
                        }
                        is GarbageSpotViewModel.GarbageSpotCreateUIState.Loading -> {
                            log.d {"Create Garbage Spot state -> Loading"}
                            CircularProgressIndicator()
                        }
                        is GarbageSpotViewModel.GarbageSpotCreateUIState.Error -> {
                            log.d {"Create Garbage Spot state -> Error"}
                            log.d {"Error -> ${createGarbageSpotState.error}"}
                            Text(
                                text = createGarbageSpotState.error,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                    }
                    Text(
                        textResource(R.string.lblCreateGarbageSpot).toString(),
                        fontSize = dimensionResource(R.dimen.txt_medium).value.sp
                    )
                    //text field for local lixo name

                    TextField(
                        value = nomeGarbageSpotState.value.text,
                        label = { Text(textResource(R.string.lblNomeGarbageSpot).toString()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = { newText ->
                            nomeGarbageSpotState.value = TextFieldValue(newText)
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
                            label = { Text(textResource(R.string.lblStatusGarbageSpot)) },
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
                    //todo redo, it bugs sometimes while placing the marker and changing name
                    //make createlocallixobutton available and assign the values inserted
                    if ((!(garbageSpot.value.latitude == "0.0" &&
                                        garbageSpot.value.longitude == "0.0")) &&
                        nomeGarbageSpotState.value.text.isNotEmpty() ){
                        garbageSpot.value.name = nomeGarbageSpotState.value.text
                        garbageSpot.value.status = selectedOptionText

                        createGarbageSpotButtonState.value = true
                    }else{
                        //error messages
                        createGarbageSpotButtonState.value = false
                        if(garbageSpot.value.latitude == "0.0" &&
                            garbageSpot.value.longitude == "0.0"){
                            Text(textResource(R.string.txtPositionError),
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(
                                    start = dimensionResource(R.dimen.medium_spacer))
                            )
                        }
                        if (nomeGarbageSpotState.value.text.isEmpty()){
                            Text(textResource(R.string.txtGarbageSpotError),
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
                        Text(garbageSpot.value.name)
                        Text(garbageSpot.value.creator.toString())
                    }
                    Spacer(Modifier.height(32.dp))
                    Column{
                        DropDownMenuStatus(
                            selectedStatus,
                            selectedId,
                            garbageSpot,
                            updateGarbageSpot,
                            bottomScaffoldState
                        )
                    }

                }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = {
                        garbageSpotViewModel.updateGarbageSpotEstado(garbageSpot.value.id,selectedStatus.value,authViewModel.tokenState.value)

                        coroutineScope.launch {
                             bottomScaffoldState.bottomSheetState.collapse()

                        }

                    },
                    enabled = updateGarbageSpot.value,
                ) {
                    Text(textResource(R.string.btnUpdateGarbageSpotStatus).toString())
                }
                GarbageSpotStatusUpdateState(
                    updateGarbageSpotState,
                    garbageSpotViewModel,
                    authViewModel,
                    log
                )
            }


        }
    }

}

@Composable
private fun GarbageSpotStatusUpdateState(
    updateGarbageSpotState: GarbageSpotViewModel.GarbageSpotUpdateUIState,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    log: Logger
) {
    when (updateGarbageSpotState) {
        is GarbageSpotViewModel.GarbageSpotUpdateUIState.Success -> {
            garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
            log.d { "Update Garbage Spot state -> Success" }
            Text(text = textResource(id = R.string.txtGarbageSpotStatusUpdateSuccess))
        }
        is GarbageSpotViewModel.GarbageSpotUpdateUIState.Loading -> {
            log.d { "Update Garbage Spot state -> Loading" }
            CircularProgressIndicator()
        }
        is GarbageSpotViewModel.GarbageSpotUpdateUIState.Error -> {
            log.d { "Update Garbage Spot state -> Error" }
            log.d { "Error -> ${updateGarbageSpotState.error}" }
            Text(text = "Update Error")
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun DropDownMenuStatus(
    selectedStatus: MutableState<String>,
    selectedId: MutableState<Long>,
    garbageSpot: MutableState<GarbageSpotDTO>,
    updateGarbageSpot: MutableState<Boolean>,
    bottomScaffoldState: BottomSheetScaffoldState
) {

    var coroutineScope = rememberCoroutineScope()

    //state of dropdownmenu
    var expanded by remember { mutableStateOf(false) }
    //available status for locallixo
    val statusList = listOf(textResource(R.string.GarbageSpotStatusListElement1), textResource(R.string.GarbageSpotStatusListElement2), textResource(R.string.GarbageSpotStatusListElement3))

    //only change selected item if the id has also changed
    if(!garbageSpot.value.id.equals(selectedId.value)){
        //changes the current locallix
        selectedId.value = garbageSpot.value.id
        //changes status to be shown
        selectedStatus.value = garbageSpot.value.status
        //update button to disabled
        updateGarbageSpot.value = false
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
                Color.White
            )

    ) {
        statusList.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                expanded = false
                //check if current garbageSpot equals the index clicked
                //if different then update the button state
                if (!statusList.indexOf(garbageSpot.value.status).equals(index)) {
                    //change status to abled so the button to update status is available
                    updateGarbageSpot.value = true
                    //expand to show update button
                    coroutineScope.launch { bottomScaffoldState.bottomSheetState.expand() }

                } else {
                    //if status has not been changed then disable the button
                    updateGarbageSpot.value = false
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


