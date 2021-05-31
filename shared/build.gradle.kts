import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.32"
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

// workaround for https://youtrack.jetbrains.com/issue/KT-43944
android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}

kotlin {
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64 else ::iosX64

    iOSTarget("ios") {}
    android()

    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }

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
                implementation(libs.logger)
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

multiplatformSwiftPackage {
    packageName("BarneeShared")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13.5") }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}