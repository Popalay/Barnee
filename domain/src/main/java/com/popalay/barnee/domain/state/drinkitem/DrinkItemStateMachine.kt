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

package com.popalay.barnee.domain.state.drinkitem

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.core.Action
import com.popalay.barnee.domain.core.EmptySideEffect
import com.popalay.barnee.domain.core.Mutation
import com.popalay.barnee.domain.core.State
import com.popalay.barnee.domain.core.StateMachine
import com.popalay.barnee.domain.state.navigation.Router
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

object DrinkItemState : State

sealed interface DrinkItemAction : Action {
    data class ToggleFavorite(val drink: Drink) : DrinkItemAction
    data class DrinkClicked(val drink: Drink) : DrinkItemAction
}

sealed interface DrinkItemMutation : Mutation {
    object Nothing : DrinkItemMutation
}

class DrinkItemStateMachine(
    collectionRepository: CollectionRepository,
    router: Router
) : StateMachine<DrinkItemState, DrinkItemAction, DrinkItemMutation, EmptySideEffect>(
    initialState = DrinkItemState,
    processor = { _, _ ->
        merge(
            filterIsInstance<DrinkItemAction.ToggleFavorite>()
                .map {
                    if (it.drink.collection == null) {
                        collectionRepository.addToCollectionAndNotify(drink = it.drink)
                    } else {
                        collectionRepository.removeFromCollectionAndNotify(it.drink)
                    }
                }
                .map { DrinkItemMutation.Nothing },
            filterIsInstance<DrinkItemAction.DrinkClicked>()
                .onEach { router.navigate(DrinkDestination(it.drink)) }
                .map { DrinkItemMutation.Nothing }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DrinkItemMutation.Nothing -> this
        }
    }
)
