package com.popalay.barnee.domain

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
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
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

interface State
interface Action
interface Mutation

@OptIn(ExperimentalCoroutinesApi::class)
abstract class StateMachine<S : State, A : Action, M : Mutation>(initialState: S) {
    private val actionFlow = MutableSharedFlow<A>(
        extraBufferCapacity = 64,
        replay = Int.MAX_VALUE,
        onBufferOverflow = DROP_OLDEST
    )
    private val stateMachineScope = MainScope()

    abstract val processor: Processor<S, M>
    abstract val reducer: Reducer<S, M>

    val stateFlow: StateFlow<S> = actionFlow
        .applyProcessor()
        .scan(initialState) { state, mutation -> reducer(state, mutation) }
        .stateIn(
            stateMachineScope,
            SharingStarted.Eagerly,
            initialState
        )

    fun onChange(provideNewState: (S) -> Unit): Closeable {
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
        stateMachineScope.launch(Dispatchers.Default) { actionFlow.emit(action) }
    }

    open fun onCleared() {
        stateMachineScope.cancel()
    }

    protected inline fun <T : Any, R : Any> Flow<T>.flatMapToResult(
        crossinline transform: suspend (value: T) -> Flow<R>
    ): Flow<Result<R>> = transformLatest { value ->
        emit(Loading<R>())
        transform(value)
            .catch { emit(Fail<R>(it)) }
            .collect { emit(Success(it)) }
    }

    private fun Flow<Action>.applyProcessor() = flatMapConcat { processor(actionFlow) { stateFlow.value } }
}

typealias Processor<State, Mutation> = Flow<Action>.(state: () -> State) -> Flow<Mutation>
typealias Reducer<State, Mutation> = State.(mutation: Mutation) -> State