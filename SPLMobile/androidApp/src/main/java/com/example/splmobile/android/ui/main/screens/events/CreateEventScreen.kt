package com.example.splmobile.android.ui.main.screens.events

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.dtos.equipments.EquipmentDTO
import com.example.splmobile.dtos.equipments.EquipmentInEventDTO
import com.example.splmobile.dtos.events.EventDTO
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.dtos.garbageTypes.GarbageTypeDTO
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.models.UserInfoViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.ktor.server.util.*
import io.ktor.util.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.*

@OptIn(ExperimentalMaterialApi::class, InternalAPI::class, InternalAPI::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateEventScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventViewModel: EventViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {
    val log = log.withTag("CreateEventScreen")


    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(Unit) {
        garbageSpotViewModel.getGarbageTypes(authViewModel.tokenState.value)
        garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
        eventViewModel.getEquipments(authViewModel.tokenState.value)
    }
    val garbageSpotsState = garbageSpotViewModel.garbageSpotsUIState.collectAsState().value
    val garbageTypesState = garbageSpotViewModel.garbageTypesUIState.collectAsState().value
    val equipmentsState = eventViewModel.equipmentUIState.collectAsState().value
    val createEventState = eventViewModel.eventCreateUIState.collectAsState().value
    val allGarbageTypeListEvent = remember { mutableStateOf(emptyList<GarbageTypeDTO>())}
    val allGarbageSpotListEvent = remember { mutableStateOf(emptyList<GarbageSpotDTO>())}
    val allEquipmentListEvent = remember { mutableStateOf(emptyList<EquipmentDTO>())}
    val listGarbageTypeInEvent = remember { mutableStateOf(SnapshotStateList<Long>())}
    val listGarbageSpotsInEvent = remember { mutableStateOf(SnapshotStateList<Long>())}
    val listEquipmentInEvent = remember { mutableStateOf(SnapshotStateList<EquipmentInEventDTO>())}



    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {textResource(R.string.lblBarCreateEvent).toString()},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "go back",
                            )
                    }

                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        {  innerPadding ->



            var nameEvent = remember { mutableStateOf(TextFieldValue("")) }
            var locationEvent = remember { mutableStateOf(LatLng(0.0,0.0)) }
            var observationsEvent = remember { mutableStateOf(TextFieldValue("")) }
            var descriptionEvent = remember { mutableStateOf(TextFieldValue("")) }
            var durationEvent = remember { mutableStateOf(TextFieldValue("")) }
            var startDateEvent = remember { mutableStateOf("") }

            // FROM VIEWMODEL
            //garbageTypeListEvent




            val accessibilityListEvent = listOf(textResource(R.string.EventAccessibilityElement1).toString(), textResource(R.string.EventAccessibilityElement2).toString(), textResource(R.string.EventAccessibilityElement3).toString())
            val quantityListEvent = listOf(textResource(R.string.EventQuantityElement1).toString(), textResource(R.string.EventQuantityElement2).toString(), textResource(R.string.EventQuantityElement3).toString())
            val restrictionsListEvent = listOf(textResource(R.string.EventRestrictionsElement1).toString(), textResource(R.string.EventRestrictionsElement2).toString())

            val accessibilityExpanded = remember { mutableStateOf(false) }
            val accessibilitySelectedOptionText = remember { mutableStateOf(accessibilityListEvent[0]) }
            val quantityExpanded = remember { mutableStateOf(false) }
            val quantitySelectedOptionText = remember { mutableStateOf(quantityListEvent[0]) }
            val restrictionsExpanded = remember { mutableStateOf(false) }
            val restrictionsSelectedOptionText = remember { mutableStateOf(restrictionsListEvent[0]) }

            val statusEvent = remember {
                mutableStateOf(TextFieldValue("Criado"))
            }

            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = innerPadding.calculateBottomPadding()),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment =  Alignment.CenterHorizontally) {


                //details section
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment =  Alignment.CenterHorizontally) {
                    val image: Painter = painterResource(id = R.drawable.ic_onboarding_participate)
                    Image(painter = image,contentDescription = "")

                    Spacer(modifier = Modifier.height(32.dp))
                    EventNameInput(nameEvent)


                    Spacer(modifier = Modifier.height(32.dp))
                    Text("PONTO DE ENCONTRO")
                    PlacePickerComponent(locationEvent,log)
                    Spacer(modifier = Modifier.height(32.dp))
                    DatePickInput(startDateEvent, log)
                    Spacer(modifier = Modifier.height(32.dp))
                    EventDurationInput(durationEvent)

                    Spacer(modifier = Modifier.height(32.dp))

                    //accessibility
                    EventAccessibilitySelection(
                        accessibilityExpanded,
                        accessibilitySelectedOptionText,
                        accessibilityListEvent
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    //quantity
                    EventQuantityExpectedSelection(
                        quantityExpanded,
                        quantitySelectedOptionText,
                        quantityListEvent
                    )

                    Spacer(modifier = Modifier.height(32.dp))


                    //restrictions
                    EventRestrictionsSelection(
                        restrictionsExpanded,
                        restrictionsSelectedOptionText,
                        restrictionsListEvent
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    when(garbageTypesState){
                        is GarbageSpotViewModel.GarbageTypesUIState.Success -> {
                            allGarbageTypeListEvent.value = garbageTypesState.garbageTypes
                            garbageTypeSelection(allGarbageTypeListEvent, listGarbageTypeInEvent)

                        }
                        is GarbageSpotViewModel.GarbageTypesUIState.Loading -> {
                            CircularProgressIndicator()
                        }
                    }

                    when(equipmentsState){
                        is EventViewModel.EquipmentUIState.Success -> {
                            allEquipmentListEvent.value = equipmentsState.equipments
                            EquipmentSelection(allEquipmentListEvent, listEquipmentInEvent)

                        }
                        is EventViewModel.EquipmentUIState.Loading -> {
                            CircularProgressIndicator()
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    EventDescriptionInput(descriptionEvent)
                    Spacer(modifier = Modifier.height(32.dp))
                    EventObservationsInput(observationsEvent)
                    Spacer(modifier = Modifier.height(32.dp))

                    when(garbageSpotsState){
                        is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
                            if( (!locationEvent.value.longitude.equals(0.0)) &&
                                (!locationEvent.value.latitude.equals(0.0))){

                                allGarbageSpotListEvent.value = garbageSpotsState.garbageSpots.filter {
                                    calculateDistance(locationEvent.value,LatLng(it.latitude.toDouble(),it.longitude.toDouble()))<50.00
                                }

                                garbageSpotsSelection(allGarbageSpotListEvent, listGarbageSpotsInEvent)
                            }

                            else{
                                Text(text="Picka a location first")
                            }



                        }
                    }


                }


                when(createEventState){
                    is EventViewModel.EventCreateUIState.Success -> {
                        log.d{"event create state -> success"}
                        Text(
                            text = textResource(R.string.txtEventCreatedSuccess).toString(),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )

                    }
                    is EventViewModel.EventCreateUIState.Error -> {
                        log.d{"event create state -> Error"}
                        log.d{"Error -> ${ createEventState.error}"}
                        Text(text = "Error message - " + createEventState.error)
                    }
                    is EventViewModel.EventCreateUIState.Loading -> CircularProgressIndicator()
                }

                Button(
                    onClick = {
                        //garbageTypeListEvent
                        if (nameEvent.value.text.isNotEmpty() &&
                            descriptionEvent.value.text.isNotEmpty() &&
                            durationEvent.value.text.isNotEmpty() &&
                            startDateEvent.value.isNotEmpty() &&
                            (!locationEvent.value.longitude.equals(0.0)) &&
                            (!locationEvent.value.latitude.equals(0.0)) &&
                            listGarbageTypeInEvent.value.isNotEmpty()
                        ) {
                            log.d { "create event fields verified" }
                            var date: Date = SimpleDateFormat("dd/MM/yyyyHH:mm").parse(startDateEvent.value)

                            startDateEvent.value = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date.toZonedDateTime())
                            eventViewModel.createEvent(
                                EventDTO(
                                    0,
                                    nameEvent.value.text,
                                    locationEvent.value.latitude.toString(),
                                    locationEvent.value.longitude.toString(),
                                    statusEvent.value.text,
                                    durationEvent.value.text,
                                    startDateEvent.value,
                                    descriptionEvent.value.text,
                                    accessibilitySelectedOptionText.value,
                                    restrictionsSelectedOptionText.value,
                                    quantitySelectedOptionText.value,
                                    observationsEvent.value.text,
                                    ""
                                    , emptyList(), emptyList(), emptyList()),
                                listGarbageSpotsInEvent.value,
                                listGarbageTypeInEvent.value,
                                listEquipmentInEvent.value,
                                authViewModel.tokenState.value
                            )



                        } else {
                            log.d { "create event fields failed" }


                        }
                    },
                ) {
                  Text(text= "Criar")
                }


            }
        },

        )


}

@Composable
private fun EquipmentSelection(
    allEquipmentListEvent: MutableState<List<EquipmentDTO>>,
    listEquipmentInEvent: MutableState<SnapshotStateList<EquipmentInEventDTO>>
) {

    Row(){
        Text(text = textResource(R.string.lblEquipment))
        Text(text = textResource(R.string.lblEquipmentIsProvided))
    }

    val listChecked = remember { mutableStateOf(SnapshotStateList<Boolean>())}
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .width(200.dp)
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(allEquipmentListEvent.value.size) { index ->
            if(listChecked.value.size < allEquipmentListEvent.value.size){
                listChecked.value.add(index,false)
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected =  listEquipmentInEvent.value.contains(
                            listEquipmentInEvent.value.find { it.equipmentID ==allEquipmentListEvent.value[index].id }
                        ),
                        onClick = {
                            Log.d("equipement", "equipment clicked")


                            var element =  listEquipmentInEvent.value.find { it.equipmentID ==allEquipmentListEvent.value[index].id }
                            Log.d("equipement", "$element")

                                if ( element !=null) {
                                    Log.d("equipment", "removing garbage type")

                                    //remove item
                                    listEquipmentInEvent.value.remove(element)


                                } else {
                                    Log.d("equipment", "adding equipment type")
                                    listEquipmentInEvent.value.add(EquipmentInEventDTO(0, 0,allEquipmentListEvent.value[index].id,listChecked.value[index],"observations"))
                                    Log.d("equipment", "list ${listEquipmentInEvent.value}")
                                }
                            }


                    )
                    .background(
                        if (  listEquipmentInEvent.value.contains(
                                listEquipmentInEvent.value.find { it.equipmentID ==allEquipmentListEvent.value[index].id }
                            )  ) Color.Gray
                        else Color.Transparent
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = allEquipmentListEvent.value[index].name)

                Checkbox(
                    checked = listChecked.value.get(index),
                    onCheckedChange = { listChecked.value[index] = it }
                )


                if ( listEquipmentInEvent.value.contains(
                        listEquipmentInEvent.value.find { it.equipmentID ==allEquipmentListEvent.value[index].id }
                    )) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                }

            }

        }
    }
}

fun calculateDistance(eventLocation: LatLng, garbageLocation: LatLng?) : Double {
    val EARTH_RADIUS = 6371 // in km

    // Convert to Radians
    val curLat = eventLocation.latitude / 180 * PI
    val curLng = eventLocation.longitude / 180 * PI
    val lastLat = garbageLocation!!.latitude / 180 * PI
    val lastLng = garbageLocation!!.longitude / 180 * PI

    // Haversine formula
    val dlng = lastLng - curLng
    val dlat = lastLat - curLat

    val a = sin(dlat/2).pow(2) + cos(curLat) * cos(lastLat) * sin(dlng / 2).pow(2)
    val c = 2 * asin(sqrt(a))
   // Log.d("event create", (c * EARTH_RADIUS).toString())


    return (c * EARTH_RADIUS)
}
@Composable
private fun EventNameInput(nameEvent: MutableState<TextFieldValue>) {
    TextField(
        value = nameEvent.value,
        onValueChange = { nameEvent.value = it },
        label = {
            Text(text = textResource(id = R.string.lblNameCreateEvent).toString())
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
    if (nameEvent.value.text.isEmpty()) {
        Text(
            text = stringResource(R.string.txtNecessaryField),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
        )
    }
}

@Composable
private fun DatePickInput(
    startDateEvent: MutableState<String>,
    log: Logger
) {
    DatePickerComponent(startDateEvent, log)

    if (startDateEvent.value.isEmpty()) {
        Text(
            text = stringResource(R.string.txtNecessaryField),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
        )
    }
}

@Composable
private fun EventDurationInput(durationEvent: MutableState<TextFieldValue>) {
    TextField(
        value = durationEvent.value,
        onValueChange = { durationEvent.value = it },
        label = {
            Text(text = textResource(id = R.string.lblDurationCreateEvent).toString())
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
    if (durationEvent.value.text.isEmpty()) {
        Text(
            text = stringResource(R.string.txtNecessaryField),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventAccessibilitySelection(
    accessibilityExpanded: MutableState<Boolean>,
    accessibilitySelectedOptionText: MutableState<String>,
    accessibilityListEvent: List<String>
) {

    ExposedDropdownMenuBox(
        expanded = accessibilityExpanded.value,
        onExpandedChange = {
            accessibilityExpanded.value = !accessibilityExpanded.value
        }
    ) {
        TextField(
            readOnly = true,
            value = accessibilitySelectedOptionText.value,
            onValueChange = { },
            label = { Text(textResource(R.string.lblAccessibilityCreateEvent).toString()) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = accessibilityExpanded.value
                )

            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = accessibilityExpanded.value,
            onDismissRequest = {
                accessibilityExpanded.value = false
            }
        ) {
            accessibilityListEvent.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        accessibilitySelectedOptionText.value = selectionOption
                        accessibilityExpanded.value = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventQuantityExpectedSelection(
    quantityExpanded: MutableState<Boolean>,
    quantitySelectedOptionText: MutableState<String>,
    quantityListEvent: List<String>
) {

    ExposedDropdownMenuBox(
        expanded = quantityExpanded.value,
        onExpandedChange = {
            quantityExpanded.value = !quantityExpanded.value
        }
    ) {
        TextField(
            readOnly = true,
            value = quantitySelectedOptionText.value,
            onValueChange = { },
            label = { Text(textResource(R.string.lblQuantityCreateEvent).toString()) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = quantityExpanded.value
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = quantityExpanded.value,
            onDismissRequest = {
                quantityExpanded.value = false
            }
        ) {
            quantityListEvent.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        quantitySelectedOptionText.value = selectionOption
                        quantityExpanded.value = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventRestrictionsSelection(
    restrictionsExpanded: MutableState<Boolean>,
    restrictionsSelectedOptionText: MutableState<String>,
    restrictionsListEvent: List<String>
) {

    ExposedDropdownMenuBox(
        expanded = restrictionsExpanded.value,
        onExpandedChange = {
            restrictionsExpanded.value = !restrictionsExpanded.value
        }
    ) {
        TextField(
            readOnly = true,
            value = restrictionsSelectedOptionText.value,
            onValueChange = { },
            label = { Text(textResource(R.string.lblRestrictionsCreateEvent).toString()) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = restrictionsExpanded.value
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = restrictionsExpanded.value,
            onDismissRequest = {
                restrictionsExpanded.value = false
            }
        ) {
            restrictionsListEvent.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        restrictionsSelectedOptionText.value = selectionOption
                        restrictionsExpanded.value = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Composable
private fun EventObservationsInput(observationsEvent: MutableState<TextFieldValue>) {
    TextField(
        value = observationsEvent.value,
        onValueChange = { observationsEvent.value = it },
        label = {
            Text(text = textResource(id = R.string.lblObservationsCreateEvent).toString())
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}

@Composable
private fun EventDescriptionInput(descriptionEvent: MutableState<TextFieldValue>) {
    TextField(
        value = descriptionEvent.value,
        onValueChange = { descriptionEvent.value = it },
        label = {
            Text(text = textResource(id = R.string.lblDescriptionCreateEvent).toString())
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
    if (descriptionEvent.value.text.isEmpty()) {
        Text(
            text = stringResource(R.string.txtNecessaryField),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
        )
    }
}

@Composable
private fun garbageTypeSelection(
    garbageTypeListEvent: MutableState<List<GarbageTypeDTO>>,
    listGarbageTypeInEvent: MutableState<SnapshotStateList<Long>>
) {
    Text(text = textResource(R.string.lblGarbageTypeCreateEvent).toString())

    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .width(200.dp)
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(garbageTypeListEvent.value.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = listGarbageTypeInEvent.value.contains(
                            garbageTypeListEvent.value.get(index).id
                        ),
                        onClick = {
                            Log.d("selection types", "garbage clicked")
                            if (listGarbageTypeInEvent.value.contains(
                                    garbageTypeListEvent.value.get(index).id
                                )
                            ) {
                                Log.d("selection types", "removing garbage type")
                                listGarbageTypeInEvent.value.remove(
                                    garbageTypeListEvent.value.get(index).id
                                )

                            } else {
                                Log.d("selection types", "adding garbage type")
                                listGarbageTypeInEvent.value.add(
                                    garbageTypeListEvent.value.get(index).id
                                )
                                Log.d("selection types", "list ${listGarbageTypeInEvent.value}")
                            }
                        }
                    )
                    .background(
                        if ((listGarbageTypeInEvent.value.contains(
                                garbageTypeListEvent.value.get(index).id
                            ))
                        ) Color.Gray
                        else Color.Transparent
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = garbageTypeListEvent.value[index].name)
                Log.d("selection types", "list ${listGarbageTypeInEvent.value}")
                Log.d("selection types", "garbage ${garbageTypeListEvent.value}")

                if ((listGarbageTypeInEvent.value.contains(garbageTypeListEvent.value[index].id))) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                }

            }

        }
    }
}

@Composable
private fun garbageSpotsSelection(
    allGarbageSpotListEvent: MutableState<List<GarbageSpotDTO>>,
    listGarbageSpotsInEvent: MutableState<SnapshotStateList<Long>>
) {
    Text(text = textResource(R.string.lblGarbageSpots).toString())


    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .width(200.dp)
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(allGarbageSpotListEvent.value.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = listGarbageSpotsInEvent.value.contains(
                            allGarbageSpotListEvent.value.get(index).id
                        ),
                        onClick = {
                            Log.d("selection types", "garbage clicked")
                            if (listGarbageSpotsInEvent.value.contains(
                                    allGarbageSpotListEvent.value.get(index).id
                                )
                            ) {
                                Log.d("selection types", "removing garbage type")
                                listGarbageSpotsInEvent.value.remove(
                                    allGarbageSpotListEvent.value.get(index).id
                                )

                            } else {
                                Log.d("selection types", "adding garbage type")
                                listGarbageSpotsInEvent.value.add(
                                    allGarbageSpotListEvent.value.get(index).id
                                )
                                Log.d("selection types", "list ${listGarbageSpotsInEvent.value}")
                            }
                        }
                    )
                    .background(
                        if ((listGarbageSpotsInEvent.value.contains(
                                allGarbageSpotListEvent.value.get(index).id
                            ))
                        ) Color.Gray
                        else Color.Transparent
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = allGarbageSpotListEvent.value[index].name)

                if ((listGarbageSpotsInEvent.value.contains(allGarbageSpotListEvent.value[index].id))) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                }

            }

        }
    }
}



@Composable
fun PlacePickerComponent(
    locationEvent: MutableState<LatLng>, log: Logger
) {
    log.d{"Picking place"}
    val dialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")

        }
    ) {
        var portugal = LatLng(39.5, -8.0)
        var cameraPosition = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(portugal, 7f)

        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            onMapClick = {
                locationEvent.value = it
            },
        ) {
            Marker(
                position = locationEvent.value,
                title = textResource(id = R.string.lblPlaceCreateEvent).toString(),
            )

        }


    }


    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment =  Alignment.CenterVertically

    ){
        // Creating a button that on
        Text(text= "Local")
        if(!(locationEvent.value.latitude.equals(0.0) && locationEvent.value.longitude.equals(0.0))){
            Text(locationEvent.value.latitude.toString())
            Text(locationEvent.value.longitude.toString())
        }
        Button(
            onClick = {
                dialogState.show()
            },
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "location   picker")
        }
    }
    if(locationEvent.value.latitude.equals(0.0) && locationEvent.value.longitude.equals(0.0)  ){
        Text(
            text = stringResource(R.string.txtNecessaryField),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
        )
    }

}


@OptIn(InternalAPI::class)
@Composable
fun DatePickerComponent(
    dateEvent: MutableState<String>,
    log: Logger
) {
    log.d{"Picking date"}
    // Fetching the Local Context
    val mContext = LocalContext.current
    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()
    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    mCalendar.time = Date()
    // Declaring a string value to
    val mTime = remember { mutableStateOf("") }
    val mDate = remember { mutableStateOf(dateEvent.value) }
    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )
    // Creating a TimePicker dialod
    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
        }, mHour, mMinute, true
    )
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment =  Alignment.CenterVertically

    ){
       // Creating a button that on
        // click displays/shows the DatePickerDialog
        Text(text= "Date")
        Text(dateEvent.value)
        Button(
            onClick = {
                mTimePickerDialog.show()
                mDatePickerDialog.show()

            },
        ) {
            Icon(Icons.Default.List, contentDescription = "DATEPICKER")
        }
    }
    dateEvent.value ="${mDate.value}${mTime.value}"





}
