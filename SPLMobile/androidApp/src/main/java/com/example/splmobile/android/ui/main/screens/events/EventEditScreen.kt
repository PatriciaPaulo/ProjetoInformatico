package com.example.splmobile.android.ui.main.screens.events

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.objects.equipments.EquipmentDTO
import com.example.splmobile.objects.equipments.EquipmentInEventDTO
import com.example.splmobile.objects.events.EventDTO
import com.example.splmobile.objects.garbageSpots.GarbageSpotDTO
import com.example.splmobile.objects.garbageTypes.GarbageTypeDTO
import com.example.splmobile.models.*
import io.ktor.server.util.*
import io.ktor.util.*
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


@OptIn(ExperimentalMaterialApi::class, InternalAPI::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EventEditScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    eventoId: String?,
    log: Logger
) {
    val log = log.withTag("EventEditScreen")

    LaunchedEffect(Unit) {
        //get all events to get info
        eventViewModel.getEventsByID(eventoId!!.toLong())
        userInfoViewModel.getMyEvents(authViewModel.tokenState.value)
        garbageSpotViewModel.getGarbageTypes(authViewModel.tokenState.value)
        garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
        eventViewModel.getEquipments(authViewModel.tokenState.value)

    }
    var eventState = eventViewModel.eventByIdUIState.collectAsState().value

    var eventUpdateState = eventViewModel.eventUpdateUIState.collectAsState().value

    val garbageSpotsState = garbageSpotViewModel.garbageSpotsUIState.collectAsState().value
    val garbageTypesState = garbageSpotViewModel.garbageTypesUIState.collectAsState().value
    val equipmentsState = eventViewModel.equipmentUIState.collectAsState().value

    val allGarbageTypeListEvent = remember { mutableStateOf(emptyList<GarbageTypeDTO>())}
    val allGarbageSpotListEvent = remember { mutableStateOf(emptyList<GarbageSpotDTO>())}
    val allEquipmentListEvent = remember { mutableStateOf(emptyList<EquipmentDTO>())}




    Scaffold(
        topBar = {
            TopAppBar(
                title = { textResource(R.string.lblEditEvent)},
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
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        when (eventState) {
            is EventViewModel.EventByIdUIState.Success -> {
                log.d{"event id state -> Success"}

                val accessibilityListEvent = listOf(textResource(R.string.EventAccessibilityElement1), textResource(R.string.EventAccessibilityElement2), textResource(R.string.EventAccessibilityElement3))
                val quantityListEvent = listOf(textResource(R.string.EventQuantityElement1), textResource(R.string.EventQuantityElement2), textResource(R.string.EventQuantityElement3))
                val restrictionsListEvent = listOf(textResource(R.string.EventRestrictionsElement1), textResource(R.string.EventRestrictionsElement2))

                val accessibilityExpanded = remember { mutableStateOf(false) }
                val accessibilitySelectedOptionText = remember { mutableStateOf(eventState.event.accessibility) }
                val quantityExpanded = remember { mutableStateOf(false) }
                val quantitySelectedOptionText = remember { mutableStateOf(eventState.event.quantity) }
                val restrictionsExpanded = remember { mutableStateOf(false) }

                val restrictionsSelectedOptionText = remember { mutableStateOf(eventState.event.restrictions) }

                val listGarbageTypeInEvent = remember { mutableStateOf(SnapshotStateList<Long>())}
                val listGarbageSpotsInEvent = remember { mutableStateOf(SnapshotStateList<Long>())}
                val listEquipamentsInEvent = remember { mutableStateOf(SnapshotStateList<EquipmentInEventDTO>())}



                Log.d("edit","${ eventState.event.garbageTypes.map { it.garbageID}}")
                Log.d("edit","${eventState.event.garbageSpots.map { it.garbageSpotID}}")
                Log.d("edit","${eventState.event.equipments}")
                listGarbageTypeInEvent.value = eventState.event.garbageTypes.map { it.garbageID}.toMutableStateList()
                listGarbageSpotsInEvent.value = eventState.event.garbageSpots.map { it.garbageSpotID}.toMutableStateList()
                listEquipamentsInEvent.value = eventState.event.equipments.toMutableStateList()
                var eventDuration = remember { mutableStateOf(TextFieldValue(eventState.event.duration)) }


                var date = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").parse(eventState.event.startDate);

                var eventStartDate = remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyyHH:mm").format(date)) }
                var eventDescription = remember { mutableStateOf(TextFieldValue(eventState.event.description)) }
                var eventObservations = remember { mutableStateOf(eventState.event.observations?.let {
                    TextFieldValue(
                        it
                    )
                }) }

                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment =  Alignment.CenterHorizontally) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Row(){
                            val image: Painter = painterResource(id = R.drawable.ic_onboarding_participate)
                            Image(painter = image, contentDescription = "")

                        }
                        Text(text = eventState.event.status, style= MaterialTheme.typography.h4)
                        Spacer(modifier = Modifier.height(32.dp))
                        DatePickerComponent(eventStartDate, log)

                        Spacer(modifier = Modifier.height(32.dp))


                        TextField(
                            value = eventDuration.value,
                            label = { Text(textResource(R.string.lblDurationCreateEvent).toString()) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = { newText ->
                                eventDuration.value = newText
                            }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        TextField(
                            value = eventDescription.value,
                            label = { Text(textResource(R.string.lblDescriptionCreateEvent)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            onValueChange = { newText ->
                                eventDescription.value = newText
                            }
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        eventObservations.value?.let {
                            TextField(
                                value = it,
                                label = { Text(textResource(R.string.lblObservationsCreateEvent).toString()) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                onValueChange = { newText ->
                                    eventObservations.value = newText
                                }
                            )
                        }
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
                        }
                        when(equipmentsState){
                            is EventViewModel.EquipmentUIState.Success -> {
                                allEquipmentListEvent.value = equipmentsState.equipments
                                EquipmentSelection(allEquipmentListEvent, listEquipamentsInEvent)
                            }
                        }



                        when(eventUpdateState){
                            is EventViewModel.EventUpdateUIState.UpdateSuccess ->{
                                Text("Update successfully")
                            }
                            is EventViewModel.EventUpdateUIState.Loading ->{
                                CircularProgressIndicator()}
                            is EventViewModel.EventUpdateUIState.Error ->{
                                Text("Error updating the event")
                            }
                        }


                        Button(
                            onClick = {


                                if (eventDuration.value.text.isNotEmpty() &&
                                    eventDescription.value.text.isNotEmpty() &&
                                    eventStartDate.value.isNotEmpty() &&
                                    listGarbageTypeInEvent.value.size>0
                                ) {
                                    log.d { "create event fields verified" }
                                    var date: Date = SimpleDateFormat("dd/MM/yyyyHH:mm").parse(eventStartDate.value)

                                    eventStartDate.value = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date.toZonedDateTime())
                                    eventViewModel.updateEvent(eventState.event.id,
                                        EventDTO(
                                            eventState.event.id,
                                            eventState.event.name,
                                            eventState.event.latitude,
                                            eventState.event.longitude,
                                            eventState.event.status,
                                            eventDuration.value.text,
                                            eventStartDate.value,
                                            eventDescription.value.text,
                                            accessibilitySelectedOptionText.value,
                                            restrictionsSelectedOptionText.value,
                                            quantitySelectedOptionText.value,
                                            eventObservations.value?.text,
                                            eventState.event.createdDate,
                                            eventState.event.garbageSpots,
                                            eventState.event.garbageTypes,
                                            eventState.event.equipments),
                                        listGarbageSpotsInEvent.value,
                                        listGarbageTypeInEvent.value,
                                        listEquipamentsInEvent.value,
                                        authViewModel.tokenState.value
                                    )



                                } else {
                                    log.d { "create event fields failed" }


                                }
                            },
                        ) {
                            Text(text= "Alterar")
                        }

                    }
                }




            }
            is EventViewModel.EventByIdUIState.Error -> {
                log.d{"event id state -> Error"}
                log.d{"Error ->${eventState.error} "}
            }
        }

    }

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
                            listEquipmentInEvent.value.find { it.equipmentID == allEquipmentListEvent.value[index].id }
                        ),
                        onClick = {
                            Log.d("equipement", "equipment clicked")


                            var element =  listEquipmentInEvent.value.find { it.equipmentID == allEquipmentListEvent.value[index].id}
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

@Composable
private fun garbageTypeSelection(
    garbageTypeListEvent: MutableState<List<GarbageTypeDTO>>,
    listGarbageTypeInEvent: MutableState<SnapshotStateList<Long>>
) {
    Text(text = textResource(R.string.lblGarbageTypeCreateEvent))

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