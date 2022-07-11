package com.example.splmobile.android.ui.main.screens.events

import MapAppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.R
import com.example.splmobile.android.textResource
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.components.SearchWidgetState
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateEventScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    garbageSpotViewModel: GarbageSpotViewModel,
    authViewModel: AuthViewModel,
    userInfoViewModel: UserInfoViewModel,
    sharedViewModel: SharedViewModel,
    log: Logger
) {


    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()




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



            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment =  Alignment.CenterHorizontally) {


                //image section
                Row() {
                    Text("imagem")
                }
                /*  name = Column(String(128), nullable=False)
                latitude = Column(Numeric(128), nullable=False)
                longitude = Column(Numeric(128), nullable=False)
                status = Column(String(50))
                duration = Column(String(50))
                startDate = Column(Date)
                description = Column(String(50))
                accessibility = Column(String(50))
                restrictions = Column(String(50))
                garbageType = Column(String(50))
                quantity = Column(String(50))
                observations = Column(String(50))*/

                //details section
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment =  Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(32.dp))


                    Text("NOME")

                    Text("PONTO DE ENCONTRO")
                    Text("startDate")
                    Text("accessibility")
                    Text("description")
                    Text("restrictions")
                    Text("garbageType")
                    Text("quantity")
                    Text("observations")
                }



            }



        },

        )


}