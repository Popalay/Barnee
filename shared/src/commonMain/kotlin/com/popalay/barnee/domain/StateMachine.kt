package com.popalay.barnee.domain

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

interface State
interface Action

abstract class StateMachine<S : State, A : Action>(initialState: S) {
    private val localStateFlow = MutableStateFlow(initialState)
    private val localActionFlow = MutableSharedFlow<A>()
    private val stateMachineScope = MainScope()
    val stateFlow: StateFlow<S>
        get() = localStateFlow

    init {
        stateMachineScope.launch {
            localActionFlow
                .collect { reducer(localStateFlow.value, it) }
        }
    }

    fun onChange(provideNewState: ((S) -> Unit)): Closeable {
        val job = Job()
        stateFlow
            .onEach { provideNewState(it) }
            .launchIn(CoroutineScope(Dispatchers.Main + job))
        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }

    fun consume(action: A) {
        stateMachineScope.launch { localActionFlow.emit(action) }
    }

    open fun onCleared() {
        stateMachineScope.cancel()
    }

    protected fun setState(body: S.() -> S) {
        localStateFlow.value = body(stateFlow.value)
    }

    protected fun awaitState(): S = stateFlow.value

    protected abstract fun reducer(currentState: S, action: A)

    protected fun <T : Any?> (suspend () -> T).execute(
        dispatcher: CoroutineDispatcher? = null,
        reducer: S.(Result<T>) -> S = { this }
    ): Job {
        setState { reducer(Loading()) }
        return stateMachineScope.launch(dispatcher ?: EmptyCoroutineContext) {
            try {
                val result = invoke()
                setState { reducer(Success(result)) }
            } catch (e: CancellationException) {
                @Suppress("RethrowCaughtException")
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                setState { reducer(Fail(e)) }
            }
        }
    }

    protected fun <T> Flow<T>.execute(
        dispatcher: CoroutineDispatcher? = null,
        reducer: S.(Result<T>) -> S = { this }
    ): Job {
        setState { reducer(Loading()) }
        return catch { error -> setState { reducer(Fail(error)) } }
            .onEach { value -> setState { reducer(Success(value)) } }
            .launchIn(stateMachineScope + (dispatcher ?: EmptyCoroutineContext))
    }
}