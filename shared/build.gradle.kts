import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version ("1.4.31")
}

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
    val sdkName: String? = System.getenv("SDK_NAME")

    val isiOSDevice = sdkName.orEmpty().startsWith("iphoneos")
    if (isiOSDevice) {
        iosArm64("iOS")
    } else {
        iosX64("iOS")
    }
    android()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(libs.ktor.core)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.logging)
            implementation(libs.logback.classic)
            implementation(libs.kotlinx.coroutines.core)
        }
        sourceSets["commonTest"].dependencies {
        }
        sourceSets["androidMain"].dependencies {
            implementation(libs.datastore.core)
            implementation(libs.datastore.runtime)
            implementation(libs.datastore.preferences)
            implementation(libs.ktor.android)
            implementation(libs.jsoup)
        }
        sourceSets["androidTest"].dependencies {
        }
        sourceSets["iOSTest"].dependencies {
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(28)
        targetSdkVersion(30)
    }
}

multiplatformSwiftPackage {
    packageName("BarneeShared")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}