package com.popalay.barnee.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface State
interface Action
interface Mutation

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
open class StateMachine<S : State, A : Action, M : Mutation>(
    initialState: S,
    initialAction: A? = null,
    processor: Processor<S, M>,
    reducer: Reducer<S, M>
) {
    private val actionFlow = MutableSharedFlow<A>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val coroutineScope: CoroutineScope = MainScope()
    private val currentState: S get() = stateFlow.value

    val stateFlow: StateFlow<S> = actionFlow
        .onStart { initialAction?.let { process(it) } }
        .flatMapConcat { processor(actionFlow) { currentState } }
        .scan(initialState) { state, mutation -> reducer(state, mutation) }
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            initialState
        )

    open fun clear() {
        coroutineScope.cancel()
    }

    fun process(action: A) {
        coroutineScope.launch { actionFlow.emit(action) }
    }
}

typealias StateProvider<State> = () -> State
typealias Processor<State, Mutation> = Flow<Action>.(StateProvider<State>) -> Flow<Mutation>
typealias Reducer<State, Mutation> = State.(mutation: Mutation) -> State