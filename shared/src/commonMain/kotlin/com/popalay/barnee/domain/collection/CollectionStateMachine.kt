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

package com.popalay.barnee.domain.collection

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class CollectionInput(val name: String) : Input

data class CollectionState(
    val name: String,
    val drinks: Flow<PagingData<Drink>> = emptyFlow()
) : State {
    constructor(input: CollectionInput) : this(input.name)
}

sealed interface CollectionAction : Action {
    object Initial : CollectionAction
}

sealed interface CollectionMutation : Mutation {
    data class Drinks(val data: Flow<PagingData<Drink>>) : CollectionMutation
}

class CollectionStateMachine(
    input: CollectionInput,
    drinkRepository: DrinkRepository
) : StateMachine<CollectionState, CollectionAction, CollectionMutation, EmptySideEffect>(
    initialState = CollectionState(input),
    initialAction = CollectionAction.Initial,
    processor = { state, _ ->
        merge(
            filterIsInstance<CollectionAction.Initial>()
                .take(1)
                .map { drinkRepository.drinks(DrinksRequest.Collection(state().name)) }
                .map { CollectionMutation.Drinks(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is CollectionMutation.Drinks -> copy(drinks = mutation.data)
        }
    }
)
