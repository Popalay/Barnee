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

package com.popalay.barnee.domain.state.shakedrink

import com.popalay.barnee.data.device.ShakeDetector
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.core.Action
import com.popalay.barnee.domain.core.EmptySideEffect
import com.popalay.barnee.domain.core.Mutation
import com.popalay.barnee.domain.core.Result
import com.popalay.barnee.domain.core.State
import com.popalay.barnee.domain.core.StateMachine
import com.popalay.barnee.domain.core.Success
import com.popalay.barnee.domain.core.Uninitialized
import com.popalay.barnee.domain.repository.DrinkRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class ShakeToDrinkState(
    val randomDrink: Result<Drink> = Uninitialized(),
    val shouldShow: Boolean = false
) : State

sealed interface ShakeToDrinkAction : Action {
    object Initial : ShakeToDrinkAction
    object DialogDismissed : ShakeToDrinkAction
    object Retry : ShakeToDrinkAction
}

sealed interface ShakeToDrinkMutation : Mutation {
    data class RandomDrink(val data: Result<Drink>) : ShakeToDrinkMutation
}

class ShakeToDrinkStateMachine(
    drinkRepository: DrinkRepository,
    shakeDetector: ShakeDetector
) : StateMachine<ShakeToDrinkState, ShakeToDrinkAction, ShakeToDrinkMutation, EmptySideEffect>(
    initialState = ShakeToDrinkState(),
    initialAction = ShakeToDrinkAction.Initial,
    processor = { state, _ ->
        merge(
            filterIsInstance<ShakeToDrinkAction.Initial>()
                .take(1)
                .flatMapMerge { detectShakes(shakeDetector) }
                .flatMapToResult { drinkRepository.randomDrink() }
                .filter { !(it is Success<Drink> && state().randomDrink is Uninitialized) }
                .map { ShakeToDrinkMutation.RandomDrink(it) },
            filterIsInstance<ShakeToDrinkAction.Retry>()
                .flatMapToResult { drinkRepository.randomDrink() }
                .map { ShakeToDrinkMutation.RandomDrink(it) },
            filterIsInstance<ShakeToDrinkAction.DialogDismissed>()
                .map { ShakeToDrinkMutation.RandomDrink(Uninitialized()) },
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is ShakeToDrinkMutation.RandomDrink -> copy(
                randomDrink = mutation.data,
                shouldShow = mutation.data !is Uninitialized
            )
        }
    }
)

private fun detectShakes(shakeDetector: ShakeDetector) = callbackFlow {
    shakeDetector.start {
        try {
            trySend(true)
        } catch (ignore: Exception) {
            // Handle exception from the channel: failure in flow or premature closing
        }
    }
    awaitClose {
        shakeDetector.stop()
    }
}
