import dev.icerock.gradle.MRVisibility

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.compose") version libs.versions.kotlin
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.compose") version libs.versions.jetbrainsCompose
    id("dev.icerock.mobile.multiplatform-resources") version libs.versions.composeResources
}

kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0"
        // Configure fields required by CocoaPods.
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ui"
            isStatic = true
        }

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.compose.resources)
                implementation(libs.koin.compose)
                implementation(libs.insetsx)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.animation)
                implementation(compose.ui)
                implementation(libs.compose.paging)
                api(libs.imageLoader)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.firebase.dynamicLinks)
                implementation(libs.youtubePlayer)
                implementation(libs.ktor.android)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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
    namespace = "com.popalay.barnee.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].java.srcDirs("build/generated/moko/androidMain/src")
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.popalay.barnee.ui"
    multiplatformResourcesClassName = "SharedRes"
    multiplatformResourcesVisibility = MRVisibility.Internal
    disableStaticFrameworkWarning = true
}
