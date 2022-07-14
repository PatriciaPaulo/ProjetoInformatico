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
import com.example.splmobile.dtos.events.EventSerializable
import com.example.splmobile.dtos.garbageTypes.GarbageTypeSerializable
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
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


    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(Unit) {
        garbageSpotViewModel.getGarbageTypes(authViewModel.tokenState.value)
    }

    var garbageTypesState = garbageSpotViewModel.garbageTypesUIState.collectAsState().value
    var createEventState = eventViewModel.eventCreateUIState.collectAsState().value
    var garbageTypeListEvent = remember { mutableStateOf(emptyList<GarbageTypeSerializable>())}


    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState


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

            when(garbageTypesState){
                is GarbageSpotViewModel.GarbageTypesUIState.Success -> {
                    garbageTypeListEvent.value = garbageTypesState.garbageTypes
                }
            }

            val accessibilityListEvent = listOf(textResource(R.string.EventAccessibilityElement1).toString(), textResource(R.string.EventAccessibilityElement2).toString(), textResource(R.string.EventAccessibilityElement3).toString())
            val quantityListEvent = listOf(textResource(R.string.EventQuantityElement1).toString(), textResource(R.string.EventQuantityElement2).toString(), textResource(R.string.EventQuantityElement3).toString())
            val restrictionsListEvent = listOf(textResource(R.string.EventRestrictionsElement1).toString(), textResource(R.string.EventRestrictionsElement2).toString())

            var accessibilityExpanded by remember { mutableStateOf(false) }
            var accessibilitySelectedOptionText by remember { mutableStateOf(accessibilityListEvent[0]) }
            var quantityExpanded by remember { mutableStateOf(false) }
            var quantitySelectedOptionText by remember { mutableStateOf(quantityListEvent[0]) }
            var restrictionsExpanded by remember { mutableStateOf(false) }
            var restrictionsSelectedOptionText by remember { mutableStateOf(restrictionsListEvent[0]) }
            var listGarbageTypeInEvent = remember { mutableStateOf(mutableListOf<Long>())}
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
                    TextField(
                        value = nameEvent.value ,
                        onValueChange = {nameEvent.value = it},
                        label = {
                            Text(text = textResource(id = R.string.lblNameCreateEvent).toString())  },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                    if(nameEvent.value.text.isEmpty()){
                        Text(
                            text = stringResource(R.string.txtNecessaryField),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )
                    }


                    Spacer(modifier = Modifier.height(32.dp))
                    Text("PONTO DE ENCONTRO")
                    PlacePickerComponent(locationEvent)
                    Spacer(modifier = Modifier.height(32.dp))
                    DatePickerComponent(startDateEvent)
                    if(startDateEvent.value.isEmpty()){
                        Text(
                            text = stringResource(R.string.txtNecessaryField),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    TextField(
                        value = durationEvent.value ,
                        onValueChange = {durationEvent.value = it},
                        label = {
                            Text(text = textResource(id = R.string.lblDurationCreateEvent).toString())  },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    if(durationEvent.value.text.isEmpty()){
                        Text(
                            text = stringResource(R.string.txtNecessaryField),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    //accessibility
                    ExposedDropdownMenuBox(
                        expanded = accessibilityExpanded,
                        onExpandedChange = {
                            accessibilityExpanded = !accessibilityExpanded
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = accessibilitySelectedOptionText,
                            onValueChange = { },
                            label = { Text(textResource(R.string.lblAccessibilityCreateEvent).toString()) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = accessibilityExpanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = accessibilityExpanded,
                            onDismissRequest = {
                                accessibilityExpanded = false
                            }
                        ) {
                            accessibilityListEvent.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        accessibilitySelectedOptionText = selectionOption
                                        accessibilityExpanded = false
                                    }
                                ) {
                                    Text(text = selectionOption)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    //quantity
                    ExposedDropdownMenuBox(
                        expanded = quantityExpanded,
                        onExpandedChange = {
                            quantityExpanded = !quantityExpanded
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = quantitySelectedOptionText,
                            onValueChange = { },
                            label = { Text(textResource(R.string.lblQuantityCreateEvent).toString()) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = quantityExpanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = quantityExpanded,
                            onDismissRequest = {
                                quantityExpanded = false
                            }
                        ) {
                            quantityListEvent.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        quantitySelectedOptionText = selectionOption
                                        quantityExpanded = false
                                    }
                                ) {
                                    Text(text = selectionOption)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))


                    //restrictions
                    ExposedDropdownMenuBox(
                        expanded = restrictionsExpanded,
                        onExpandedChange = {
                            restrictionsExpanded = !restrictionsExpanded
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = restrictionsSelectedOptionText,
                            onValueChange = { },
                            label = { Text(textResource(R.string.lblRestrictionsCreateEvent).toString()) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = restrictionsExpanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = restrictionsExpanded,
                            onDismissRequest = {
                                restrictionsExpanded = false
                            }
                        ) {
                            restrictionsListEvent.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        restrictionsSelectedOptionText = selectionOption
                                        restrictionsExpanded = false
                                    }
                                ) {
                                    Text(text = selectionOption)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
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
                                            if (listGarbageTypeInEvent.value.contains(
                                                    garbageTypeListEvent.value.get(index).id
                                                )
                                            ) {
                                                listGarbageTypeInEvent.value.remove(
                                                    garbageTypeListEvent.value.get(index).id
                                                )
                                            } else {
                                                listGarbageTypeInEvent.value.add(
                                                    garbageTypeListEvent.value.get(index).id
                                                )
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
                                if((listGarbageTypeInEvent.value.contains(garbageTypeListEvent.value.get(index).id))){
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

                    Spacer(modifier = Modifier.height(32.dp))


                    Spacer(modifier = Modifier.height(32.dp))
                    TextField(
                        value = descriptionEvent.value ,
                        onValueChange = {descriptionEvent.value = it},
                        label = {
                            Text(text = textResource(id = R.string.lblDescriptionCreateEvent).toString())  },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                    if(descriptionEvent.value.text.isEmpty()){
                        Text(
                            text = stringResource(R.string.txtNecessaryField),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    TextField(
                        value = observationsEvent.value ,
                        onValueChange = {observationsEvent.value = it},
                        label = {
                            Text(text = textResource(id = R.string.lblObservationsCreateEvent).toString())  },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                    if(observationsEvent.value.text.isEmpty()){
                        Text(
                            text = stringResource(R.string.txtNecessaryField),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )
                    }
                }


                when(createEventState){
                    is EventViewModel.EventCreateUIState.Success -> {
                        Text(
                            text = textResource(R.string.txtEventCreatedSuccess).toString(),
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                        )

                    }
                    is EventViewModel.EventCreateUIState.Error -> Text(text = "Error message - " + createEventState.error)
                    is EventViewModel.EventCreateUIState.Loading -> CircularProgressIndicator()
                }

                Button(onClick = {
                    //garbageTypeListEvent
                    if(nameEvent.value.text.isNotEmpty() &&
                        observationsEvent.value.text.isNotEmpty() &&
                        descriptionEvent.value.text.isNotEmpty() &&
                        durationEvent.value.text.isNotEmpty() &&
                        startDateEvent.value.isNotEmpty() &&
                        (!locationEvent.value.longitude.equals(0.0)) &&
                        (!locationEvent.value.latitude.equals(0.0)) &&
                        listGarbageTypeInEvent.value .isNotEmpty()
                    ){
                        Log.d("create event screen","verificated")
                        eventViewModel.createEvent(EventSerializable(
                            0,
                            nameEvent.value.text,
                            locationEvent.value.latitude.toString(),
                            locationEvent.value.longitude.toString(),
                            statusEvent.value.text,
                            durationEvent.value.text,
                            startDateEvent.value,
                            descriptionEvent.value.text,
                            accessibilitySelectedOptionText,
                            restrictionsSelectedOptionText,
                            quantitySelectedOptionText,
                            observationsEvent.value.text
                            ),
                            listGarbageTypeInEvent.value,
                         authViewModel.tokenState.value)
                    }else{
                        Log.d("create event screen","failed check fields")
                    }
                },  ) {
                  Text(text= "Criar")
                }


            }
        },

        )


}

@Composable
fun PlacePickerComponent(locationEvent: MutableState<LatLng>) {

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
        Button(onClick = {
            dialogState.show()
        },  ) {
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


@Composable
fun DatePickerComponent(
    dateEvent: MutableState<String>
) {
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
    val mDate = remember { mutableStateOf("") }
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
        }, mHour, mMinute, false
    )
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment =  Alignment.CenterVertically

    ){
       // Creating a button that on
        // click displays/shows the DatePickerDialog
        Text(text= "Date")
        Text(dateEvent.value)
        Button(onClick = {
            mTimePickerDialog.show()
            mDatePickerDialog.show()

        },  ) {
            Icon(Icons.Default.List, contentDescription = "DATEPICKER")
        }
    }
    dateEvent.value ="${mDate.value} ${mTime.value}"


}
