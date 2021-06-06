import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.5.10"
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

kotlin {
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64 else ::iosX64

    iOSTarget("ios") {}
    android()

    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
    }

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("kotlinx.coroutines.FlowPreview")
            }
        }
        configurations {
            all {
                exclude("com.russhwolf", "multiplatform-settings-coroutines")
            }
        }

        commonMain {
            dependencies {
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.logging)
                implementation(libs.logback.classic)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.settings)
                implementation(libs.settingscoroutines)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.datastore.core)
                implementation(libs.datastore.runtime)
                implementation(libs.datastore.preferences)
                implementation(libs.ktor.android)
                implementation(libs.settingsdatastore)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.ios)
            }
        }
    }
}

android {
    compileSdk = 30
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 28
        targetSdk = 30
    }
}