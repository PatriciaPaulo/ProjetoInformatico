package com.example.splmobile.android.ui.main.screens.garbageSpots


import AppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.ui.main.screens.events.EventsList
import com.example.splmobile.android.ui.navigation.Screen
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.objects.garbageSpots.GarbageSpotDTO
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.GarbageSpotViewModel
import com.example.splmobile.models.UserInfoViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GarbageSpotsListScreen(
    navController: NavHostController,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    mainViewModel: MainViewModel,
    log: Logger
) {
    val log = log.withTag("GarbageSpotsListScreen")
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(Unit) {
        garbageSpotViewModel.getGarbageSpots(authViewModel.tokenState.value)
    }


    //search bar states
    val searchWidgetState by mainViewModel.searchWidgetState
    val searchTextState by mainViewModel.searchTextState


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(
                title = textResource(R.string.txtGarbageSpotsList),
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

                },
                onSearchTriggered = {
                    mainViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)

                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content =
        { innerPadding ->
            var garbageSpotsListState =
                garbageSpotViewModel.garbageSpotsUIState.collectAsState().value

            when (garbageSpotsListState) {
                is GarbageSpotViewModel.GarbageSpotsUIState.Success -> {
                    log.d { "Get garbage spots state -> Success" }
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = innerPadding.calculateBottomPadding())
                    ) {
                        if(searchTextState.isNotEmpty()){

                            items(garbageSpotsListState.garbageSpots.filter { (
                                    it.approved || it.creator == userInfoViewModel.myIdUIState.value) && (it.name.contains(searchTextState) ||
                                    (it.status.contains(searchTextState))

                                    )
                            }.size) { index ->
                                GarbageSpotsList(
                                    gs = garbageSpotsListState.garbageSpots.filter { (it.approved || it.creator == userInfoViewModel.myIdUIState.value) &&(it.name.contains(searchTextState) ||
                                            (it.status.contains(searchTextState)) )}.get(index),
                                    navController = navController
                                )
                            }
                        }else{
                            items(garbageSpotsListState.garbageSpots.filter { it.approved || it.creator == userInfoViewModel.myIdUIState.value }.size) { index ->
                                GarbageSpotsList(
                                    gs = garbageSpotsListState.garbageSpots.filter { it.approved || it.creator == userInfoViewModel.myIdUIState.value }.get(index),
                                    navController = navController
                                )
                            }
                        }

                    }
                }
                is GarbageSpotViewModel.GarbageSpotsUIState.Error -> {
                    log.d { "Get garbage spots state -> Error" }
                    Text(
                        text = textResource(R.string.txtGarbageSpotError),
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.medium_spacer))
                    )
                }
                is GarbageSpotViewModel.GarbageSpotsUIState.Loading -> CircularProgressIndicator()
            }


        },

        )

}

@Composable
fun GarbageSpotsList(navController: NavHostController, gs: GarbageSpotDTO) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                onClick = {
                    navController.navigate(Screen.GarbageSpotInfo.route + "/${gs.id}")
                },

                )


    ) {
        Image(painter = painterResource(id = R.drawable.ic_main_map), contentDescription = null)
        Column() {
            Text(text = gs.name, style = MaterialTheme.typography.h6)
            Text(text = gs.status, style = MaterialTheme.typography.body1)
           // Text(text = gs.approved.toString(), style = MaterialTheme.typography.body2)

        }
    }
    Divider(color = Color.Gray, thickness = 1.dp)

}