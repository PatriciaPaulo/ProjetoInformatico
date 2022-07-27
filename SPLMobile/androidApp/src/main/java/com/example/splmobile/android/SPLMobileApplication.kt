package com.example.splmobile.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.splmobile.AppInfo
import com.example.splmobile.android.viewmodel.MapViewModel
import com.example.splmobile.initKoin
import com.example.splmobile.models.ActivityViewModel
import com.example.splmobile.models.AuthViewModel
import com.example.splmobile.models.EventViewModel
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.userInfo.UserInfoViewModel
import com.example.splmobile.models.garbageSpots.GarbageSpotViewModel

import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidApplication

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

@HiltAndroidApp
class SPLMobileApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@SPLMobileApplication }
                viewModel { GarbageSpotViewModel(get(), get { parametersOf("LocalLixoViewModel") }, get { parametersOf("LocalLixoRepository") }) }
                viewModel { SharedViewModel(get(), get { parametersOf("SharedViewModel") }) }
                viewModel { UserInfoViewModel(get(), get { parametersOf("UserInfoViewModel") }) }
                viewModel { EventViewModel(get(), get { parametersOf("EventViewModel") }) }
                viewModel { AuthViewModel(get(), get { parametersOf("AuthViewModel") }) }
                viewModel { ActivityViewModel(get(), get { parametersOf("ActivityViewModel") }) }
                viewModel { MapViewModel(androidApplication()) }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("KAMPSTARTER_SETTINGS", Context.MODE_PRIVATE)
                }
                single<AppInfo> { AndroidAppInfo }
                single {
                    { Log.i("Startup", "Hello from Android/Kotlin!") }
                }
            }
        )
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
