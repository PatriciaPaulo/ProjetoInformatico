package com.example.splmobile.android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController

import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.navigation.SetupNavGraph
import com.example.splmobile.android.ui.theme.SPLTheme
import com.example.splmobile.android.viewmodel.MainViewModel
import com.example.splmobile.android.viewmodel.SplashViewModel
import com.example.splmobile.injectLogger
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
//import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import javax.inject.Inject


private const val TAG = "MainActivity"
const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
@ExperimentalAnimationApi
@ExperimentalPagerApi
@AndroidEntryPoint
class ActivityMain : ComponentActivity() , KoinComponent{
    @Inject
    lateinit var splashViewModel: SplashViewModel
    private val log: Logger by injectLogger("MainActivity")
    private val localLixoViewModel: LocalLixoViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition{
            !splashViewModel.isLoading.value
        }
        setContent {
            SPLTheme() {
                val screen by splashViewModel.startDestination

                val navController = rememberNavController()

                // Apply the padding globally to the whole BottomNavScreensController
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



    }
}
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