package com.example.splmobile.android

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.navigation.SetupNavGraph
import com.example.splmobile.android.ui.theme.SPLTheme
import com.example.splmobile.android.viewmodel.*
import com.example.splmobile.injectLogger
import com.example.splmobile.viewmodels.*
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
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
    //koin models
    private val garbageSpotViewModel: GarbageSpotViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()
    private val userInfoViewModel: UserInfoViewModel by viewModel()
    private val eventViewModel: EventViewModel by viewModel()
    private val activityViewModel: ActivityViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private val friendViewModel: FriendViewModel by viewModel()
    private val messageViewModel: MessageViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModels()
    private val mapViewModel : MapViewModel by viewModels<MapViewModel>()
    private val cameraViewModel : CameraViewModel by viewModels()

    //notifcations system
    private val channel1IDfriendRequest = "package com.example.splmobile.android.channel1"
    private val channel1IDeventStatus= "package com.example.splmobile.android.channel2"

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
                    userViewModel = userViewModel,
                    friendViewModel = friendViewModel,
                    messageViewModel = messageViewModel,
                    cameraViewModel = cameraViewModel,
                )

            }

            // The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
            // The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
            val PERMISSION_ALL = 1
            val PERMISSIONS = arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA
            )

            prepLocationUpdates()
            prepStepCounter()
            requestCameraPermission()
            readExternalStorage()




            //builds and waits for notifications
            eventStatusChanged(messageViewModel,channel1IDeventStatus,applicationContext)
            friendRequestReceived(messageViewModel,channel1IDfriendRequest,applicationContext)
        }






    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("PERMS", "Permission granted")
        } else {
            Log.i("PERMS", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("PERMS", "Camera permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("PERMS", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun readExternalStorage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            println("readExternalStorage")
        } else {
            println("readExternalStorage")
            requestSinglePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }


    private fun prepStepCounter() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            println("prepStepCounter")
        } else {
            println("prepStepCounter")
            requestSinglePermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
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
        }
    }

    private fun requestLocationUpdates() {
        println("requestLocationUpdates")
        mapViewModel.startLocationUpdates()
    }

}

@Composable
fun friendRequestReceived(
    messageViewModel: MessageViewModel,
    channel: String,
    context: Context
){

    when(val requestState = messageViewModel.notiReceivedUIState.collectAsState().value){
        is MessageViewModel.NotificationUIState.SuccessFriendRequestReceived->{
            println("friend request received in activity main")


            val builder = NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_onboarding_participate)
                .setContentTitle("Novo pedido de amizade")
                .setContentText("O utilizador ${requestState} quer ser seu amigo!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channel,
                    "SPL",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                // Register the channel with the system
                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                notificationManager.notify(requestState.user.id.toInt(),builder.build());
            }

        }
    }

}

@Composable
fun eventStatusChanged(
    messageViewModel: MessageViewModel,
    channel: String,
    context: Context
){

    when(val requestState = messageViewModel.notiReceivedUIState.collectAsState().value){
        is MessageViewModel.NotificationUIState.SuccessEventStatus->{
            println("event status received in activity main")
           val builder = NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_onboarding_participate)
                .setContentTitle("Estado de um evento alterado")
                .setContentText("O estado do evento ${requestState.event.name} foi alterado para ${requestState.event.status}")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channel,
                    "SPL",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                // Register the channel with the system
                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                notificationManager.notify(requestState.event.id.toInt(),builder.build());

            }
        }
    }



}
// Retrieve string id
@Composable
@ReadOnlyComposable
fun textResource(@StringRes id: Int) : String =
    LocalContext.current.resources.getText(id).toString()

