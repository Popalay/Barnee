import dev.icerock.gradle.MRVisibility
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.compose") version libs.versions.jetbrainsCompose
    id("dev.icerock.mobile.multiplatform-resources") version libs.versions.composeResources
}

kotlin {
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64 else ::iosX64

    iOSTarget("ios") {}
    android()

    sourceSets {
        commonMain {
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
        sourceSets["androidMain"].dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.firebase.dynamicLinks)
            implementation(libs.youtubePlayer)
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.popalay.barnee.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        consumerProguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    kotlin {
        jvmToolchain(19)
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.popalay.barnee.ui"
    multiplatformResourcesClassName = "SharedRes"
    multiplatformResourcesVisibility = MRVisibility.Internal
}
