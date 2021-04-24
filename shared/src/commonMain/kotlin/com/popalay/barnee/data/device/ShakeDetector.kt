package com.popalay.barnee.data.device

expect class ShakeDetector {
    fun start(listener: () -> Unit): Boolean
    fun stop()
}