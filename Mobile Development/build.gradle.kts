// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false // Firebase auth
    id("com.google.devtools.ksp") version "1.9.0-1.0.11" apply false //dengan asumsi versi Kotlin 1.9.0
}