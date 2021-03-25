package com.popalay.barnee.domain

sealed class Result<out T>(val complete: Boolean, val shouldLoad: Boolean, private val value: T?) {
    open operator fun invoke(): T? = value
}

class Uninitialized<out T> : Result<T>(complete = false, shouldLoad = true, value = null)

data class Loading<out T>(private val value: T? = null) : Result<T>(complete = false, shouldLoad = false, value = value)

data class Success<out T>(private val value: T) : Result<T>(complete = true, shouldLoad = false, value = value) {
    override operator fun invoke(): T = value
}

data class Fail<out T>(val error: Throwable, private val value: T? = null) : Result<T>(complete = true, shouldLoad = true, value = value) {
    override fun equals(other: Any?): Boolean {
        if (other !is Fail<*>) return false

        val otherError = other.error
        return error::class == otherError::class &&
                error.message == otherError.message &&
                error.stackTraceToString() == otherError.stackTraceToString()
    }

    override fun hashCode(): Int = arrayOf(error::class, error.message, error.stackTraceToString()).contentHashCode()
}