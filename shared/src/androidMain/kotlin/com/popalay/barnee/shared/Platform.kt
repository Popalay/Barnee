package com.popalay.barnee.shared

actual class Platform actual constructor() {
    actual val platformName: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}