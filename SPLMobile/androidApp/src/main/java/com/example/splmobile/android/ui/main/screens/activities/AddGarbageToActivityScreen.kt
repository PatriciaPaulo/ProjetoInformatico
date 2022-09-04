package com.example.splmobile.android.ui.main.screens.activities

import BackAppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.ListItemUI

import com.example.splmobile.objects.activities.ExplicitGarbageInActivityDTO
import com.example.splmobile.objects.activities.GarbageAmountDTO
import com.example.splmobile.objects.activities.GarbageInActivityDTO
import com.example.splmobile.isNumber
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.objects.garbageTypes.GarbageTypeDTO
import com.example.splmobile.objects.garbageTypes.UnitTypeDTO

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddGarbageToActivity(
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    activityViewModel: ActivityViewModel,
    navController : NavController,
    log: Logger
) {
    val log = log.withTag("ManageGarbageInActivity")

    val title = stringResource(R.string.manageGarbage)
    Scaffold(
        topBar = {
            BackAppBar(title, navController)
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = {
            addGBA(garbageSpotViewModel, authViewModel, activityViewModel, log)
        }
    )
}

@Composable
fun addGBA(garbageSpotViewModel: GarbageSpotViewModel,
           authViewModel: AuthViewModel,
           activityViewModel: ActivityViewModel,
           log: Logger) {
    val token = authViewModel.tokenState.value
    val currentActivity = activityViewModel.getCurrentActivity()

    var showError by remember { mutableStateOf(false) }
    var errorString by remember { mutableStateOf("") }
    val dataMissing =  stringResource(R.string.dataMissing)
    var showErrorState = { source : Int ->
        showError = true
        if(source == 1){
            errorString = dataMissing
        }
    }
    var resetErrorState = {
        showError = false
    }

    LaunchedEffect(Unit) {
        log.d { "Get Garbage Type and Unit Type and Garbage in Current Activity" }
        garbageSpotViewModel.getGarbageTypes(token)
        garbageSpotViewModel.getUnitTypes()
        activityViewModel.getGarbageInActivity(currentActivity, token)
    }

    var garbageTypesState = garbageSpotViewModel.garbageTypesUIState.collectAsState().value
    var garbageTypeList = remember { mutableStateOf(emptyList<GarbageTypeDTO>()) }
    var garbageTypeListExpanded = remember { mutableStateOf(false) }
    val garbageTypeString = stringResource(R.string.selecioneLixo)
    var garbageTypeSelected = remember { mutableStateOf(GarbageTypeDTO(0, garbageTypeString)) }

    when (garbageTypesState) {
        is GarbageSpotViewModel.GarbageTypesUIState.Success -> {
            log.d { "Garbage Types Collected" }
            garbageTypeList.value = garbageTypesState.garbageTypes
        }
        is GarbageSpotViewModel.GarbageTypesUIState.Error -> {
            log.d { "Garbage Types GET Failed" }
            showError = true
            errorString = stringResource(R.string.activityBDError)
        }
    }

    var garbageQuantity by remember { mutableStateOf("") }
    var garbageQuantityUpdate = { data : String ->
        garbageQuantity = data
    }

    var unitTypeState = garbageSpotViewModel.unitTypeUIState.collectAsState().value
    var unitTypeList = remember { mutableStateOf(emptyList<UnitTypeDTO>()) }
    var unitTypeListExpanded = remember { mutableStateOf(false) }
    val unitTypeString = stringResource(R.string.selecioneUT)
    var unitTypeSelected = remember { mutableStateOf(UnitTypeDTO(0, unitTypeString)) }

    when (unitTypeState) {
        is GarbageSpotViewModel.UnitTypesUIState.Success -> {
            log.d { "Unit Types Collected" }
            unitTypeList.value = unitTypeState.unitTypes
        }
        is GarbageSpotViewModel.UnitTypesUIState.Error -> {
            log.d { "Unit Types GET Failed" }
            showError = true
            errorString = stringResource(R.string.activityBDError)
        }
    }


    var garbageList by remember { mutableStateOf(emptyList<ExplicitGarbageInActivityDTO>()) }
    var getGarbageInActivityState = activityViewModel.garbageInActivityUIState.collectAsState().value

    when (getGarbageInActivityState) {
        is ActivityViewModel.GarbageInActivityUIState.Success -> {
            log.d { "Garbage in Activity Collected" }
            garbageList = getGarbageInActivityState.activities
        }

        is ActivityViewModel.GarbageInActivityUIState.Error -> {

        }
    }


    var postGarbageState = activityViewModel.addGarbageInActivityUIState.collectAsState().value
    when(postGarbageState) {
        is ActivityViewModel.AddGarbageInActivityUIState.Success -> {
            activityViewModel.getGarbageInActivity(activityViewModel.getCurrentActivity(), token)
        }
        is ActivityViewModel.AddGarbageInActivityUIState.Error -> {
            showError = true
        }
    }

    var patchGarbageState = activityViewModel.updateGarbageInActivityUIState.collectAsState().value
    when(patchGarbageState) {
        is ActivityViewModel.UpdateGarbageInActivityUIState.Success -> {
            activityViewModel.getGarbageInActivity(activityViewModel.getCurrentActivity(), token)
        }
        is ActivityViewModel.UpdateGarbageInActivityUIState.Error -> {
            showError = true
        }
    }

    var deleteGarbage = activityViewModel.deleteGarbageInActivity.collectAsState().value
    when(deleteGarbage) {
        is ActivityViewModel.DeleteGarbageInActivityUIState.Success -> {
            activityViewModel.getGarbageInActivity(activityViewModel.getCurrentActivity(), token)
        }
        is ActivityViewModel.DeleteGarbageInActivityUIState.Error -> {
            // TODO Handle Error
        }
    }

    AddGarbageToActivityUI(
        garbageTypeList,
        garbageTypeListExpanded,
        garbageTypeSelected,
        garbageQuantity,
        garbageQuantityUpdate,
        unitTypeList,
        unitTypeListExpanded,
        unitTypeSelected,
        garbageList,
        showError,
        showErrorState,
        resetErrorState,
        errorString,
        activityViewModel,
        token
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddGarbageToActivityUI(
    garbageTypeList : MutableState<List<GarbageTypeDTO>>,
    garbageTypeListExpanded : MutableState<Boolean>,
    garbageTypeSelected : MutableState<GarbageTypeDTO>,
    garbageQuantity : String,
    garbageQuantityUpdate: (String) -> Unit,
    unitTypeList : MutableState<List<UnitTypeDTO>>,
    unitTypeListExpanded : MutableState<Boolean>,
    unitTypeSelected : MutableState<UnitTypeDTO>,
    garbageList: List<ExplicitGarbageInActivityDTO>,
    showError: Boolean,
    showErrorState : (Int) -> Unit,
    resetErrorState : () -> Unit,
    errorString: String,
    activityViewModel: ActivityViewModel,
    token: String
) {
    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.small_spacer))
    ) {
        // Register Collected Trash
        Box(
            modifier = Modifier
                .padding(bottom = dimensionResource(R.dimen.small_spacer))
        ) {
            Column (
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.small_spacer))
            ) {
                // Sub Title
                Text(
                    stringResource(R.string.addGarbage),
                    fontSize = dimensionResource(R.dimen.small_title).value.sp,
                    style = MaterialTheme.typography.subtitle1,
                )

                Row(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.small_spacer))
                ) {
                    // Select Garbage Type
                    ExposedDropdownMenuBox(
                        expanded = garbageTypeListExpanded.value,
                        onExpandedChange = {
                            garbageTypeListExpanded.value = !garbageTypeListExpanded.value
                        },
                    ) {
                        TextField(
                            readOnly = true,
                            value = garbageTypeSelected.value.name,
                            onValueChange = { },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = garbageTypeListExpanded.value
                                )

                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = garbageTypeListExpanded.value,
                            onDismissRequest = {
                                garbageTypeListExpanded.value = false
                            }
                        ) {
                            garbageTypeList.value.forEach { selectedOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        garbageTypeSelected.value = selectedOption
                                        garbageTypeListExpanded.value = false
                                        resetErrorState()
                                    }
                                ) {
                                    Text(text = selectedOption.name)
                                }
                            }
                        }
                    }

                    val selectGarbageString = stringResource(R.string.selecioneLixo)
                    val selectUnitType = stringResource(R.string.selecioneUT)
                    IconButton(
                        onClick = {
                            if(garbageTypeSelected.value.name == selectGarbageString ||
                                unitTypeSelected.value.name == selectUnitType ||
                                garbageQuantity.isEmpty() || !isNumber(garbageQuantity)) {
                                showErrorState(1)
                            } else {
                                val explicitSelected = ExplicitGarbageInActivityDTO(
                                    0,
                                    garbageTypeSelected.value.name,
                                    garbageQuantity.toFloat(),
                                    unitTypeSelected.value.name
                                )
                                val garbageSelected = GarbageInActivityDTO(
                                    0,
                                    activityViewModel.getCurrentActivity().toLong(),
                                    garbageTypeSelected.value.id,
                                    garbageQuantity.toFloat(),
                                    unitTypeSelected.value.id
                                )

                                saveGarbageCollected(
                                    explicitSelected,
                                    garbageSelected,
                                    garbageList,
                                    activityViewModel,
                                    token
                                )
                            }
                        },
                        enabled = !showError
                    ) {
                        Icon(
                            Icons.Default.Check,
                            "",
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(R.dimen.small_spacer))
                ) {
                    // Type quantity
                    OutlinedTextField(
                        modifier = Modifier
                            .width(dimensionResource(R.dimen.txtBoxSmall)),
                        value = garbageQuantity,
                        onValueChange = { garbageQuantityUpdate(it)
                                        resetErrorState()},
                        placeholder = { Text("0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Type Unit
                    ExposedDropdownMenuBox(
                        expanded = unitTypeListExpanded.value,
                        onExpandedChange = {
                            unitTypeListExpanded.value = !unitTypeListExpanded.value
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = unitTypeSelected.value.name,
                            onValueChange = {  },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = unitTypeListExpanded.value
                                )

                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = unitTypeListExpanded.value,
                            onDismissRequest = {
                                unitTypeListExpanded.value = false
                            }
                        ) {
                            unitTypeList.value.forEach { selectedOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        unitTypeSelected.value = selectedOption
                                        unitTypeListExpanded.value = false
                                        resetErrorState()
                                    }
                                ) {
                                    Text(text = selectedOption.name)
                                }
                            }
                        }
                    }
                }

                if(showError) {
                        Text(
                            text = errorString,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                        )
                }
            }
        }

        GarbageInActivity(garbageList, activityViewModel, token)
    }
}

@Composable
private fun GarbageInActivity(
    garbage: List<ExplicitGarbageInActivityDTO>,
    activityViewModel: ActivityViewModel,
    token: String
) {
    var noGarbage by remember { mutableStateOf(false) }

    // Sub Title
    Text(
        stringResource(R.string.listGarbage),
        fontSize = dimensionResource(R.dimen.small_title).value.sp,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .padding(bottom = dimensionResource(R.dimen.small_spacer))
    )

    if(noGarbage) {
        Text(
            stringResource(R.string.noGarbage),
            fontSize = dimensionResource(R.dimen.txt_large).value.sp,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.secondary
        )
    }

    LazyColumn(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.small_spacer))
    ) {
        if (garbage.isEmpty()) {
            noGarbage = true
        } else {
            noGarbage = false
            garbage.forEachIndexed { index, garbage ->
                item {
                    Box(){
                        var gb_amount : String = garbage.amount.toString()
                        var gb_unit : String = garbage.unit

                        if(garbage.unit == "unidade"){
                            gb_amount = garbage.amount.toInt().toString()
                        }
                        if(garbage.amount > 1) {
                            gb_unit = garbage.unit + "s"
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        ) {
                            ListItemUI(garbage.garbage, "$gb_amount $gb_unit")
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                        ) {
                            IconButton(
                                onClick = { activityViewModel.deleteGarbageInActivity(garbage.id, token)}
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    "",
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}


private fun saveGarbageCollected(
    newGarbageComparison : ExplicitGarbageInActivityDTO,
    newGarbage : GarbageInActivityDTO,
    garbageList : List<ExplicitGarbageInActivityDTO>,
    activityViewModel: ActivityViewModel,
    token: String
) {
    garbageList.forEach { item ->
        if (newGarbageComparison.garbage == item.garbage && newGarbageComparison.unit == item.unit) {
            // PUT Garbage
            //putFlag = true
            val putData = GarbageAmountDTO(newGarbage.amount + item.amount)

            activityViewModel.patchGarbageInActivity(
                activityViewModel.getCurrentActivity().toLong(),
                item.id,
                putData,
                token
            )
            return
        }
    }

    activityViewModel.postGarbageInActivity(
        newGarbage,
        activityViewModel.getCurrentActivity().toLong(),
        token
    )
}