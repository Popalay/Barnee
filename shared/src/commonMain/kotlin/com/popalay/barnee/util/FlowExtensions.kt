package com.popalay.barnee.util

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

fun <T> StateFlow<T>.wrap(): CFlow<T> = CFlow(this)

class CFlow<T>(private val origin: StateFlow<T>) : StateFlow<T> by origin {
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()

        onEach { block(it) }
            .launchIn(MainScope() + job)

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}