/*
 * Copyright (c) 2021 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.domain.core

import com.popalay.barnee.domain.log.StateMachineLogger
import com.popalay.barnee.domain.util.CFlow
import com.popalay.barnee.domain.util.wrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val sideEffectChannel = Channel<SE>()
    private val stateMachineScope = MainScope()
    private val currentState: S get() = (stateFlow.unwrap() as StateFlow<S>).value
    private val logger by inject<StateMachineLogger>()

    val sideEffectFlow: CFlow<SE> = sideEffectChannel.receiveAsFlow().wrap()
    val stateFlow: CFlow<S> = actionFlow
        .onStart { initialAction?.let { emit(it) } }
        .onEach { logger.log(this, it) }
        .processor({ currentState }, {
            sideEffectChannel.send(it)
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
