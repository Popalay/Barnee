package com.popalay.barnee.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
interface SideEffect

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
open class StateMachine<S : State, A : Action, M : Mutation, SE : SideEffect>(
    initialState: S,
    initialAction: A? = null,
    processor: Processor<S, M, SE>,
    reducer: Reducer<S, M>
) {
    private val actionFlow = MutableSharedFlow<A>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val _sideEffectFlow = MutableSharedFlow<SE>()
    private val coroutineScope: CoroutineScope = MainScope()
    private val currentState: S get() = stateFlow.value

    val stateFlow: StateFlow<S> = actionFlow
        .onStart { initialAction?.let { process(it) } }
        .flatMapConcat { actionFlow.processor({ currentState }, { _sideEffectFlow.emit(it) }) }
        .scan(initialState) { state, mutation -> reducer(state, mutation) }
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            initialState
        )
    val sideEffectFlow: SharedFlow<SE> = _sideEffectFlow

    open fun clear() {
        coroutineScope.cancel()
    }

    fun process(action: A) {
        coroutineScope.launch { actionFlow.emit(action) }
    }
}

typealias StateProvider<State> = () -> State
typealias SideEffectConsumer<SideEffect> = suspend (SideEffect) -> Unit
typealias Processor<State, Mutation, SideEffect> = Flow<Action>.(StateProvider<State>, SideEffectConsumer<SideEffect>) -> Flow<Mutation>
typealias Reducer<State, Mutation> = State.(mutation: Mutation) -> State