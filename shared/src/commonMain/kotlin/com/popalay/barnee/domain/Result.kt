package com.popalay.barnee.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transformLatest

sealed class Result<out T>(val complete: Boolean, val shouldLoad: Boolean, private val value: T?) {
    open operator fun invoke(): T? = value
}

class Uninitialized<out T> : Result<T>(complete = false, shouldLoad = true, value = null)

data class Loading<out T>(private val value: T? = null) : Result<T>(complete = false, shouldLoad = false, value = value)

data class Success<out T>(private val value: T) : Result<T>(complete = true, shouldLoad = false, value = value) {
    override operator fun invoke(): T = value
}

data class Fail<out T>(val error: Throwable, private val value: T? = null) :
    Result<T>(complete = true, shouldLoad = true, value = value) {
    override fun equals(other: Any?): Boolean {
        if (other !is Fail<*>) return false

        val otherError = other.error
        return error::class == otherError::class &&
                error.message == otherError.message &&
                error.stackTraceToString() == otherError.stackTraceToString()
    }

    override fun hashCode(): Int = arrayOf(error::class, error.message, error.stackTraceToString()).contentHashCode()
}

val <T : Any?> Result<T>.isEmpty
    get() = when {
        this() is List<*> -> (this() as List<*>).isEmpty()
        else -> this() == null
    }

inline fun <T : Any?, R : Any?> Result<T>.map(crossinline transform: (value: T) -> R): Result<R> =
    when (this) {
        is Uninitialized -> Uninitialized()
        is Loading -> Loading()
        is Success -> Success(transform(this()))
        is Fail -> Fail(error)
    }

inline fun <T : Any, R : Any> Flow<T>.flatMapToResult(
    crossinline transform: suspend (value: T) -> Flow<R>
): Flow<Result<R>> = transformLatest { value ->
    emit(Loading())
    transform(value)
        .catch { emit(Fail<R>(it) as Result<R>) }
        .collect { emit(Success(it)) }
}.catch { emit(Fail(it)) }
