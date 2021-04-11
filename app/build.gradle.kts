import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, libs.compose.compiler)
    implementation(project(":shared"))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.coil)
    implementation(libs.accompanist.insets)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.lottie.compose)
    implementation(libs.exoplayer)
    implementation(libs.youtubeExtractor)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.palette)
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.popalay.barnee"
        minSdkVersion(28)
        targetSdkVersion(30)
        versionCode = properties.getOrDefault("barnee.versioncode", 1).toString().toInt()
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
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.OptIn"
        )
    }
}