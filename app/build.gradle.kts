/*
 * Copyright (c) 2023 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
}

dependencies {
    implementation(project(":shared"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.youtubePlayer)
    implementation(libs.paging.compose)
    implementation(libs.coil.compose)
    implementation(libs.firebase.dynamicLinks)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.vojager)
}

val isCI = System.getenv("CI") == "true"
println("Is CI environment: $isCI")

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.popalay.barnee"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = properties.getOrDefault("barnee.versioncode", 1).toString().toInt()
        versionName = "1.3.0"

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
    }

    lint {
        checkReleaseBuilds = false
        checkDependencies = true
        ignoreTestSources = true
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_19.toString()
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        kotlinOptions.freeCompilerArgs += "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi"
    }

    packagingOptions {
        resources.excludes.add("META-INF/INDEX.LIST")
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
    namespace = "com.popalay.barnee"
}
