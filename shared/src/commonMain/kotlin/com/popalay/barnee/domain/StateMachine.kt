package com.popalay.barnee.domain

import com.popalay.barnee.util.prettyPrint
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import mu.KotlinLogging

interface State
interface Action
interface Output

@OptIn(ExperimentalCoroutinesApi::class)
abstract class StateMachine<S : State, A : Action, R : Output>(initialState: S) {
    private val actionFlow = MutableSharedFlow<A>(extraBufferCapacity = 64, replay = 1)
    private val stateMachineScope = MainScope()
    private val logger by lazy { KotlinLogging.logger { } }

    val stateFlow: StateFlow<S>

    abstract val processor: Processor<S, R>
    abstract val reducer: Reducer<S, R>

    init {
        stateFlow = actionFlow
            .onEach { log(it) }
            .applyProcessor()
            .scan(initialState) { state, result -> reducer(state, result) }
            .onEach { log(it) }
            .stateIn(
                stateMachineScope,
                SharingStarted.Eagerly,
                initialState
            )
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

    fun process(action: A) {
        stateMachineScope.launch { actionFlow.emit(action) }
    }

    open fun onCleared() {
        stateMachineScope.cancel()
    }

    protected inline fun <T : Any, R : Any> Flow<T>.mapToResult(
        crossinline transform: suspend (value: T) -> R
    ): Flow<Result<R>> = transform { value ->
        emit(Loading<R>())
        try {
            emit(Success(transform(value)))
        } catch (exception: Exception) {
            emit(Fail<R>(exception))
        }
    }

    protected inline fun <T : Any, R : Any> Flow<T>.flatMapToResult(
        crossinline transform: suspend (value: T) -> Flow<R>
    ): Flow<Result<R>> = transform { value ->
        emit(Loading<R>())
        transform(value)
            .catch { emit(Fail<R>(it)) }
            .collect { emit(Success(it)) }
    }

    @OptIn(FlowPreview::class)
    private fun Flow<Any>.applyProcessor() = flatMapConcat { processor(actionFlow) { stateFlow.value } }

    private fun log(value: Any) {
        when (value) {
            is State -> logger.debug { "New state --> ${value.prettyPrint()}" }
            is Action -> logger.debug { "Action processed --> ${value.prettyPrint()}" }
        }
    }
}

typealias Processor<State, Result> = Flow<Any>.(state: () -> State) -> Flow<Result>
typealias Reducer<State, R> = State.(result: R) -> State