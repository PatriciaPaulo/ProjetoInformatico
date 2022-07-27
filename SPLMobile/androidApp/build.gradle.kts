plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android")
    id ("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.example.splmobile.android"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.guava:guava:27.0.1-android")

    //Coroutines Dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")

    //Map Dependencies
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("com.google.maps.android:maps-compose:1.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:20.0.0")

    val composeVersion = "1.1.1"
    val koin_version = "3.2.0"
    val lifecycle_version = "2.4.1"
    //Jetpack Compose Dependencies
        // Integration with activities
    implementation ("androidx.activity:activity-compose:1.4.0")
        // Compose Material Design
    implementation ("androidx.compose.material:material:$composeVersion")
        // Animations
    implementation ("androidx.compose.animation:animation:$composeVersion")
        // Tooling support (Previews, etc.)
    implementation ("androidx.compose.ui:ui-tooling:$composeVersion")
        // Integration with ViewModels
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    // UI Tests
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$composeVersion")
        // KOIN
        implementation ("io.insert-koin:koin-androidx-compose:$koin_version")
        // Koin Core features
        implementation ("io.insert-koin:koin-core:$koin_version")
    // Koin Test features
        testImplementation ("io.insert-koin:koin-test:$koin_version")
        // Jetpack Compose
        implementation ("io.insert-koin:koin-androidx-compose:$koin_version")
        // Koin for Ktor
        implementation ("io.insert-koin:koin-ktor:$koin_version")
    // SLF4J Logger
        implementation ("io.insert-koin:koin-logger-slf4j:$koin_version")
    //Splash API
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")

    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.4.2")

    // Accompanist for Pager and Indicators
    implementation("com.google.accompanist:accompanist-pager:0.23.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.23.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")

    //Dagger Hilt for Dependencies Injection
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt ("com.google.dagger:hilt-android-compiler:2.38.1")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation ("androidx.compose.runtime:runtime-livedata:$composeVersion")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // Lifecycles only (without ViewModel or LiveData)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

    // Saved state module for ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

    // Annotation processor
    kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    //swipe refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.9-beta")
    //serialization
    val serializationVersion = "1.3.0"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    //json serialization
    val ktorVersion = "2.0.1"
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    //dialogs
    implementation ("io.github.vanpra.compose-material-dialogs:core:0.7.0")
}