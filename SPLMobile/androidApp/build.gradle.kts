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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("com.google.guava:guava:27.0.1-android")

    //Coroutines Dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    //Map Dependencies
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")

    //Jetpack Compose Dependencies
    val composeVersion = "1.1.1"
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

    //Splash API
    implementation("androidx.core:core-splashscreen:1.0.0-beta02")

    //Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.4.2")

    // Accompanist for Pager and Indicators
    implementation("com.google.accompanist:accompanist-pager:0.23.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.23.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Dagger Hilt for Dependencies Injection
    implementation("com.google.dagger:hilt-android:2.38.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt ("com.google.dagger:hilt-android-compiler:2.38.1")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")


}