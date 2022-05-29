package com.example.splmobile.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController

import co.touchlab.kermit.Logger
import com.example.splmobile.android.ui.main.BottomNavigationBar
import com.example.splmobile.android.ui.main.navigation.Navigation
import com.example.splmobile.android.viewmodel.SplashViewModel
import com.example.splmobile.injectLogger
import com.example.splmobile.models.LixeiraViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
//import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
//import javax.inject.Inject

private const val TAG = "MainActivity"
const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
@ExperimentalAnimationApi
@ExperimentalPagerApi

class ActivityMain : ComponentActivity() , KoinComponent{
    private val log: Logger by injectLogger("MainActivity")
    private val lixeiraViewModel: LixeiraViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()
            val navController = rememberNavController()
            Scaffold(/*scaffoldState = scaffoldState,*/
                bottomBar = { BottomNavigationBar(navController = navController) }
            ) {

                Navigation(navController = navController,log, lixeiraViewModel = lixeiraViewModel )
            }
        }
    }
}

/*SPLTheme {
               val screen by splashViewModel.startDestination

               // rememberNav to know if its the first time to show onboarding
               val navController = rememberNavController()

               SetupNavGraph(
                   navController = navController,
                   startDestination = screen
               )
           }*/
       /*
       setContentView(R.layout.activity_maps_filter)
        val rvMap: RecyclerView = findViewById(R.id.rvMap)
        val userMaps: MutableList<UserMap> = arrayListOf()
        var lixeiras: MutableList<Lixeira> = arrayListOf()

        val mainScope = MainScope()

        mainScope.launch {
            kotlin.runCatching {
                getLixeiras()
            }.onSuccess {
                lixeiras = it
                //todas as lixeiras
                val uMapTodas = UserMap("todas", lixeiras)
                userMaps.add(uMapTodas)

                //as minhas lixeiras
                kotlin.runCatching {
                    currentUser(intent.getStringExtra("token").toString())
                }.onSuccess {
                    val currentUserObj = JSONObject(it)
                    // Log.i(TAG,"current user id  ${  currentUserObj.get("id")}")
                    val uMapMinhas = UserMap(
                        "minhas",
                        lixeiras.filter { currentUserObj.get("id") == it.criador })
                    userMaps.add(uMapMinhas)
                }
                // lixeiras com estado "Limpo"
                val uMapEstadoLimpo = UserMap("Limpo", lixeiras.filter { s -> s.estado == "Limpo" })
                userMaps.add(uMapEstadoLimpo)
                // lixeiras com estado "Muito sujo"
                val uMapEstadoMtSujo =
                    UserMap("Muito sujo", lixeiras.filter { s -> s.estado == "Muito sujo" })
                userMaps.add(uMapEstadoMtSujo)


                rvMap.layoutManager = LinearLayoutManager(this@MainActivity)
                Log.i(TAG, "foreach ${intent.getStringExtra("token")}")

                rvMap.adapter =
                    MapsAdapter(this@MainActivity, userMaps, object : MapsAdapter.OnClickListener {
                        override fun onItemClick(position: Int) {
                            Log.i(TAG, "onItemClick $position")
                            Log.i(TAG, "~lixeira" + userMaps.get(0))
                            val intent = Intent(this@MainActivity, ActivityMap::class.java)
                            intent.putExtra(EXTRA_USER_MAP, userMaps[position])
                            startActivity(intent)
                        }
                    })
            }
        }

    }*/

    /*suspend fun getLixeiras(): MutableList<Lixeira> {
        val mainScope = MainScope()
        var rsCode = 0
        var rsBody = ""

        var lixeiras: MutableList<Lixeira> = mutableListOf<Lixeira>()


        val (responseCode, responseBody) = Lixeiras().getLixeiras()
        rsCode = responseCode
        rsBody = responseBody

        when (rsCode) {
            200 -> {
                //Log.i("LIXEIRAS", rsBody)
                try {
                    val array = JSONArray(rsBody)
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        lixeiras.add(
                            Lixeira(
                                item.getString("nome"),
                                item.getString("latitude"),
                                item.getString("longitude"),
                                item.getString("estado"),
                                item.getString("criador")
                            )
                        )
                        Log.i("lixeira:", item.toString())
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }
            else -> {
                Log.i("MAINACTIVITY", "erro " + rsBody)
            }
        }
        return lixeiras;
    }

    suspend fun currentUser(token :String): String {
        val mainScope = MainScope()
        var user = ""
        var rsCode = 0
        var rsBody = ""

        val (responseCode, responseBody) = Authentication().getLoggedInUser(token)
        rsCode = responseCode
        rsBody = responseBody

        when (rsCode) {
            200 ->   {
                user = rsBody
            }
            else -> {
                //TODO style elements, text to red etc
                Log.i(TAG,"ERROR ON GET LOGGED IN USER")
            }
        }
        return user
    }
}*/