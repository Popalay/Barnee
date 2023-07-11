/*
 * Copyright (c) 2023 Denys Nykyforov
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

package com.popalay.barnee.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.popalay.barnee.domain.log.StateMachineLogger
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.navigationSideEffect
import com.popalay.barnee.util.CFlow
import com.popalay.barnee.util.wrap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface State
object NoState : State
interface Action

object InitialAction : Action

abstract class StateMachine<S : State>(
    initialState: S,
    reducer: Reducer<S>,
) : KoinComponent, ScreenModel {
    private val sharedActionFlow = MutableSharedFlow<Action>()
    private val currentState: S get() = (stateFlow.unwrap() as StateFlow<S>).value
    private val logger by inject<StateMachineLogger>()
    private val router by inject<Router>()

    val stateFlow: CFlow<S> = sharedActionFlow
        .onStart { emit(InitialAction) }
        .onEach { logger.log(this, it) }
        .let {
            merge(
                it.reducer({ currentState }, ::dispatch),
                it.navigationSideEffect({ currentState }, router)
            )
        }
        .onEach { logger.log(this, it) }
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            initialState
        )
        .wrap()

    fun dispatch(action: Action) {
        coroutineScope.launch { sharedActionFlow.emit(action) }
    }
}

typealias StateProvider<State> = () -> State
typealias ActionDispatcher = suspend (Action) -> Unit
typealias Reducer<State> = Flow<Action>.(StateProvider<State>, ActionDispatcher) -> Flow<State>
