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
    id("kotlin-parcelize")
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
                implementation(libs.koin.core)
                implementation(libs.multiplatformsettings.noarg)
                implementation(libs.multiplatformsettings.coroutines)
                implementation(libs.openai.client)
                implementation(libs.bundles.vojager)
                implementation(libs.uuid)
                implementation(libs.bundles.kotlinx)
                api(libs.parcelize)
                api(libs.uri)
                api(libs.multiplatformpaging)
            }
        }

        sourceSets["commonTest"].dependencies {
            implementation(kotlin("test"))
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
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
}

buildkonfig {
    packageName = android.namespace

    defaultConfigs {
        val secrets = listOf(getSecret("OPEN_AI_API_KEY"), getSecret("CLOUDINARY_API_SECRET"))
        secrets.forEach { (secretName, secretValue) ->
            buildConfigField(
                type = FieldSpec.Type.STRING,
                name = secretName,
                value = secretValue,
                const = true,
                nullable = false
            )
        }
    }
}

// https://youtrack.jetbrains.com/issue/KT-55751
configurations {
    val myAttribute = Attribute.of("dummy.attribute", String::class.java)

    named("podDebugFrameworkIosFat") { attributes.attribute(myAttribute, "dummy-value") }
    named("podReleaseFrameworkIosFat") { attributes.attribute(myAttribute, "dummy-value") }
}

fun getSecret(name: String): Pair<String, String> {
    val secret = System.getenv(name)
        ?: gradleLocalProperties(rootDir).getProperty(name)
        ?: error("No $name provided")
    return name to secret
}
