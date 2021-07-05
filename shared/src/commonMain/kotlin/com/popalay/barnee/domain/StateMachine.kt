package com.popalay.barnee.domain

import com.popalay.barnee.domain.log.StateMachineLogger
import com.popalay.barnee.util.CFlow
import com.popalay.barnee.util.wrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface Input
interface State
interface Action
interface Mutation
interface SideEffect
object EmptySideEffect : SideEffect

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
open class StateMachine<S : State, A : Action, M : Mutation, SE : SideEffect>(
    initialState: S,
    initialAction: A? = null,
    processor: Processor<S, M, SE>,
    reducer: Reducer<S, M>
) : KoinComponent {
    private val actionFlow = MutableSharedFlow<A>()
    private val _sideEffectFlow = MutableSharedFlow<SE>()
    private val stateMachineScope = MainScope()
    private val currentState: S get() = (stateFlow.unwrap() as StateFlow<S>).value
    private val logger by inject<StateMachineLogger>()

    val sideEffectFlow: CFlow<SE> = _sideEffectFlow.asSharedFlow().wrap()
    val stateFlow: CFlow<S> = actionFlow
        .onStart { initialAction?.let { emit(it) } }
        .onEach { logger.log(this, it) }
        .processor({ currentState }, {
            _sideEffectFlow.emit(it)
            logger.log(this, it)
        })
        .onEach { logger.log(this, it) }
        .map { reducer(currentState, it) }
        .onEach { logger.log(this, it) }
        .stateIn(
            stateMachineScope,
            SharingStarted.Eagerly,
            initialState
        ).wrap()

    open fun clear() {
        stateMachineScope.coroutineContext.cancelChildren()
    }

    fun process(action: A) {
        stateMachineScope.launch { actionFlow.emit(action) }
    }
}

typealias StateProvider<State> = () -> State
typealias SideEffectConsumer<SideEffect> = suspend (SideEffect) -> Unit
typealias Processor<State, Mutation, SideEffect> = Flow<Action>.(StateProvider<State>, SideEffectConsumer<SideEffect>) -> Flow<Mutation>
typealias Reducer<State, Mutation> = State.(mutation: Mutation) -> State
