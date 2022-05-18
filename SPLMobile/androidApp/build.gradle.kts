plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("android")
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
        // Integration with activities
    implementation ("androidx.activity:activity-compose:1.4.0")
        // Compose Material Design
    implementation ("androidx.compose.material:material:1.1.1")
        // Animations
    implementation ("androidx.compose.animation:animation:1.1.1")
        // Tooling support (Previews, etc.)
    implementation ("androidx.compose.ui:ui-tooling:1.1.1")
        // Integration with ViewModels
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
        // UI Tests
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:1.1.1")
}