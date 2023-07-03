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

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.codingfeline.buildkonfig") version libs.versions.buildkonfig.gradle.plugin
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
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("com.russhwolf.settings.ExperimentalSettingsApi")
            }
        }

        commonMain {
            dependencies {
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.contentNegotiation)
                implementation(libs.ktor.json)
                implementation(libs.logback.classic)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.koin.core)
                implementation(libs.multiplatformsettings.noarg)
                implementation(libs.multiplatformsettings.coroutines)
                implementation(libs.openai.client)
                implementation(libs.uri)
                implementation(libs.uuid)
                api(libs.multiplatformpaging)
            }
        }

        sourceSets["androidMain"].dependencies {
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.runtime)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.ktor.android)
            implementation(libs.multiplatformsettings.datastore)
            implementation(libs.firebase.dynamicLinks)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.ktor.ios)
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("proguard-rules.pro")
    }
    namespace = "com.popalay.barnee.shared"

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

buildkonfig {
    packageName = android.namespace

    val openApiKeyName = "OPEN_AI_API_KEY"
    val openAiApiKey = System.getenv(openApiKeyName)
        ?: gradleLocalProperties(rootDir).getProperty(openApiKeyName)
        ?: error("No $openApiKeyName provided")

    defaultConfigs {
        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = openApiKeyName,
            value = openAiApiKey,
            const = true,
            nullable = false
        )
    }
}

// https://youtrack.jetbrains.com/issue/KT-55751
configurations {
    val myAttribute = Attribute.of("dummy.attribute", String::class.java)

    named("podDebugFrameworkIosFat") { attributes.attribute(myAttribute, "dummy-value") }
    named("podReleaseFrameworkIosFat") { attributes.attribute(myAttribute, "dummy-value") }
}
