import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
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

    val isWatchOSDevice = sdkName.orEmpty().startsWith("watchos")
    if (isWatchOSDevice) {
        watchosArm64("watch")
    } else {
        watchosX86("watch")
    }

    macosX64("macOS")
    android()
    jvm()

    sourceSets {
        sourceSets["commonMain"].dependencies {
        }
        sourceSets["commonTest"].dependencies {
        }
        sourceSets["androidMain"].dependencies {
        }
        sourceSets["androidTest"].dependencies {
        }
        sourceSets["jvmMain"].dependencies {
        }
        sourceSets["iOSMain"].dependencies {
        }
        sourceSets["iOSTest"].dependencies {
        }
        sourceSets["watchMain"].dependencies {
        }
        sourceSets["macOSMain"].dependencies {
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