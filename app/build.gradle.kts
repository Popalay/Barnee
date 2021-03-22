plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization") version ("1.4.31")
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.coil)
    implementation(libs.accompanist.insets)
    implementation(libs.datastore.runtime)
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)
    implementation(libs.lottie.compose)
    implementation(libs.ktor.android)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.jsoup)
    implementation(libs.mavericks.compose)
    implementation(libs.exoplayer)
    implementation(libs.youtubeExtractor)
    implementation(libs.logback.classic)
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.popalay.barnee"
        minSdkVersion(28)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta02"
    }
}