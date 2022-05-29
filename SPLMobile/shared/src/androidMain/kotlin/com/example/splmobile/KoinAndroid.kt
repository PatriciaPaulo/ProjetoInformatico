package com.example.splmobile

import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.okhttp.OkHttp

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            AppDatabase.Schema,
            get(),
            "AppDatabase"
        )
    }

    single<Settings> {
        AndroidSettings(get())
    }

    single {
        OkHttp.create()
    }
}
