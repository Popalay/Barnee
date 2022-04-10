/*
 * Copyright (c) 2022 Denys Nykyforov
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

import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
}

dependencies {
    add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, libs.androidx.compose.compiler)
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.exoplayer)
    implementation(libs.youtubeExtractor) {
        exclude("com.android.support", "support-annotations")
    }
    implementation(libs.paging.compose)
    implementation(libs.coil.compose)
    implementation(libs.firebase.dynamicLinks)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.bundles.accompanist)
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
        versionName = "1.1.5"

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
            checkReleaseBuilds = false
            checkDependencies = true
            ignoreTestSources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.get()
    }
    namespace = "com.popalay.barnee"
}
