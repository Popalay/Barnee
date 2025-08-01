/*
 * Copyright (c) 2025 Denys Nykyforov
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

package com.popalay.barnee.domain.navigation

import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateProvider
import com.popalay.barnee.util.ignoreElements
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

sealed interface StackChangeAction : Action
data class NavigateToAction(val screen: TypedScreenProvider) : StackChangeAction
data class ReplaceCurrentScreenAction(val screen: TypedScreenProvider) : StackChangeAction
object NavigateBackAction : StackChangeAction

internal fun <T : State> Flow<Action>.navigationSideEffect(state: StateProvider<T>, router: Router): Flow<T> =
    filterIsInstance<StackChangeAction>()
        .onEach {
            when (it) {
                is NavigateToAction           -> router.updateStack(StackChange.Push(it.screen))
                is ReplaceCurrentScreenAction -> router.updateStack(StackChange.Replace(it.screen))
                is NavigateBackAction         -> router.updateStack(StackChange.Pop)
            }
        }
        .map { state() }
        .ignoreElements()
