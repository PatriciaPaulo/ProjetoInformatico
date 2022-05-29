plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.squareup.sqldelight")

}

version = "1.0"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val ktorVersion = "2.0.1"
        val koinVersion = "3.2.0"
        val coroutinesVersion = "1.6.2"
        val serializationVersion = "1.3.0"
        val sqlDelightVersion= "1.5.3"
        val lifecycle_version = "2.4.1"

        val commonMain by getting {
            dependencies {

                //ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                //logging requests
                implementation("io.ktor:ktor-client-logging:$ktorVersion")

                //kotlinx
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                //Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
                //datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

                //koin

                implementation("io.insert-koin:koin-core:$koinVersion")
                 //SQL DELIGHT
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation ("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

                // stately
                implementation ("co.touchlab:stately-common:1.2.1")
                // settings repo
                implementation("com.russhwolf:multiplatform-settings:0.9")

                //kermit logger
                implementation(kotlin("stdlib-common"))
                api("co.touchlab:kermit:1.0.3")





            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
                // ViewModel
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
                // ViewModel utilities for Compose
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")




            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                //Data storage
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

    }

}
sqldelight {
    database("AppDatabase") {
        packageName = "com.example.splmobile"
    }
}
android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 30
        targetSdk = 32
    }
}
dependencies {
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.databinding:compiler:3.2.0-alpha11")
}

