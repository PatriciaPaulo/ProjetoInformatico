package com.example.splmobile.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.splmobile.AppInfo
import com.example.splmobile.initKoin
import com.example.splmobile.models.SharedViewModel
import com.example.splmobile.models.locaisLixo.LocalLixoViewModel

import dagger.hilt.android.HiltAndroidApp

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
                viewModel { LocalLixoViewModel(get(), get { parametersOf("LocalLixoViewModel") }) }
                viewModel { SharedViewModel(get(), get { parametersOf("SharedViewModel") }) }
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
