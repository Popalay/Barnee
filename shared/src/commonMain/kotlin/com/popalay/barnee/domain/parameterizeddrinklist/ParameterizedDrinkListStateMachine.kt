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

package com.popalay.barnee.domain.parameterizeddrinklist

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class ParameterizedDrinkListInput(
    val request: DrinksRequest,
    val title: String,
    val emptyStateMessage: String,
    val titleHighlighted: String = ""
) : Input

data class ParameterizedDrinkListState(
    val request: DrinksRequest,
    val title: String,
    val titleHighlighted: String,
    val emptyStateMessage: String,
    val drinks: Flow<PagingData<Drink>> = emptyFlow()
) : State {
    constructor(input: ParameterizedDrinkListInput) : this(input.request, input.title, input.titleHighlighted, input.emptyStateMessage)
}

sealed interface ParameterizedDrinkListAction : Action {
    object Initial : ParameterizedDrinkListAction
}

class ParameterizedDrinkListStateMachine(
    input: ParameterizedDrinkListInput,
    drinkRepository: DrinkRepository
) : StateMachine<ParameterizedDrinkListState, ParameterizedDrinkListAction, EmptySideEffect>(
    initialState = ParameterizedDrinkListState(input),
    initialAction = ParameterizedDrinkListAction.Initial,
    reducer = { state, _ ->
        merge(
            filterIsInstance<ParameterizedDrinkListAction.Initial>()
                .take(1)
                .map { drinkRepository.drinks(state().request) }
                .map { state().copy(drinks = it) }
        )
    }
)
