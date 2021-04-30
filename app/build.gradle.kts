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
    implementation(libs.youtubeExtractor) {
        exclude("com.android.support", "support-annotations")
    }
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
}

val isCI = System.getenv("CI") == "true"
println("Is CI environment: $isCI")

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "com.popalay.barnee"
        minSdk = 28
        targetSdk = 30
        versionCode = properties.getOrDefault("barnee.versioncode", 1).toString().toInt()
        versionName = "1.0.3"

        signingConfigs {
            getByName("debug") {
                storeFile = file("../release/debug.keystore")
            }
            register("release") {
                storeFile = file("../release/release.keystore")
                keyAlias = "barnee"
                storePassword = System.getenv("ANDROID_RELEASE_KEYSTORE_PWD").orEmpty()
                keyPassword = System.getenv("ANDROID_RELEASE_KEY_PWD").orEmpty()
            }
        }

        buildTypes {
            getByName("debug") {
                signingConfig = signingConfigs.getByName("debug")
                versionNameSuffix = "-dev"
                applicationIdSuffix = ".debug"
            }

            getByName("release") {
                signingConfig = if (isCI) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }

        lint {
            isCheckReleaseBuilds = false
            isCheckDependencies = true
            isIgnoreTestSources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.OptIn"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}