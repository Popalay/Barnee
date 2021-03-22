plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.coil)
    implementation(libs.accompanist.insets)
    implementation(libs.lottie.compose)
    implementation(libs.mavericks.compose)
    implementation(libs.exoplayer)
    implementation(libs.youtubeExtractor)
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
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta02"
    }
}