package com.popalay.barnee.util

interface Logger {
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

object EmptyLogger : Logger {
    override fun debug(tag: String, message: String, throwable: Throwable?) = Unit
    override fun info(tag: String, message: String, throwable: Throwable?) = Unit
    override fun warn(tag: String, message: String, throwable: Throwable?) = Unit
    override fun error(tag: String, message: String, throwable: Throwable?) = Unit
}

@Suppress("EmptyDefaultConstructor")
expect class RealLogger() : Logger
