buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    val kotlinVersion = "1.6.10"
    val sqlDelightVersion: String by project
    dependencies {
        // Use of Kotlin 1.6.10 so we can use Jetpack Composer
        classpath("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")

    }

}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
