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

package com.popalay.barnee.domain.discovery

import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class DiscoveryState(
    val categories: Result<List<Category>> = Uninitialized()
) : State

sealed interface DiscoveryAction : Action {
    object Initial : DiscoveryAction
}

sealed interface DiscoveryMutation : Mutation {
    data class Categories(val data: Result<List<Category>>) : DiscoveryMutation
}

class DiscoveryStateMachine(
    drinkRepository: DrinkRepository,
) : StateMachine<DiscoveryState, DiscoveryAction, DiscoveryMutation, EmptySideEffect>(
    initialState = DiscoveryState(),
    initialAction = DiscoveryAction.Initial,
    processor = { _, _ ->
        merge(
            filterIsInstance<DiscoveryAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.categories() }
                .map { DiscoveryMutation.Categories(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DiscoveryMutation.Categories -> copy(categories = mutation.data)
        }
    }
)
