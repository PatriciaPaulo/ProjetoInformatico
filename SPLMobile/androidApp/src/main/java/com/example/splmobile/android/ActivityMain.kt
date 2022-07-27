package com.example.splmobile.android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController

import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.navigation.SetupNavGraph
import com.example.splmobile.android.ui.theme.SPLTheme
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.android.viewmodel.SplashViewModel
import com.example.splmobile.injectLogger
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
//import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import javax.inject.Inject


private const val TAG = "MainActivity"
const val EXTRA_USER_MAP = "EXTRA_USER_MAP"

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")

const val EMAIL_KEY = "email"
const val PASSWORD_KEY = "password"

@ExperimentalAnimationApi
@ExperimentalPagerApi
@AndroidEntryPoint
class ActivityMain : ComponentActivity() , KoinComponent {
    @Inject
    lateinit var splashViewModel: SplashViewModel
    private val log: Logger by injectLogger("MainActivity")
    private val garbageSpotViewModel: GarbageSpotViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()
    private val userInfoViewModel: UserInfoViewModel by viewModel()
    private val eventViewModel: EventViewModel by viewModel()
    private val activityViewModel: ActivityViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModels()
    private val mapViewModel : MapViewModel by viewModels<MapViewModel>()



    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // installs SplashScreen while application is loading
        installSplashScreen().setKeepOnScreenCondition{
            !splashViewModel.isLoading.value
        }

        setContent {
            SPLTheme() {
                // Gets the start destination screen
                val screen by splashViewModel.startDestination

                val navController = rememberNavController()

                SetupNavGraph(
                    navController = navController,
                    startDestination = screen,
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    garbageSpotViewModel = garbageSpotViewModel,
                    userInfoViewModel = userInfoViewModel,
                    eventViewModel = eventViewModel,
                    sharedViewModel = sharedViewModel,
                    activityViewModel = activityViewModel,
                    mapViewModel = mapViewModel,
                    log = log,
                )
            }
            prepLocationUpdates()
        }
    }

    private fun prepLocationUpdates() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            println("prepLocationUpdates")
            requestLocationUpdates()
        } else {
            println("prepLocationUpdates")
            requestSinglePermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestSinglePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if (isGranted) {
            requestLocationUpdates()
        } else {
            // Default Location (Center of Portugal)
            var defaultLocation = LatLng(39.5, -8.0)

            var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



        }
    }

    private fun requestLocationUpdates() {
        println("requestLocationUpdates")
        mapViewModel.startLocationUpdates()
    }
}


// Retrieve string id
@Composable
@ReadOnlyComposable
fun textResource(@StringRes id: Int) : CharSequence =
    LocalContext.current.resources.getText(id)

/*setContent {
           SPLTheme() {
               val screen by splashViewModel.startDestination

               val navController = rememberNavController()
               Scaffold(
                   bottomBar = { BottomNavigationBar(navController = navController)}
               ) { innerPadding ->
                   // Apply the padding globally to the whole BottomNavScreensController
                   Box(modifier = Modifier.padding(innerPadding)) {
                       SetupNavGraph(
                           navController = navController,
                           startDestination = screen,
                           log,
                           mainViewModel = mainViewModel,
                           localLixoViewModel = localLixoViewModel,
                           sharedViewModel = sharedViewModel
                       )
                   }

               }

           }*/