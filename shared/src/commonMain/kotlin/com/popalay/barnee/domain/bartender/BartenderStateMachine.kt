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

package com.popalay.barnee.domain.bartender

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.NoSideEffect
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.navigateBack
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

data class BartenderState(
    val generatedDrink: Result<Drink> = Uninitialized(),
    val prompt: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) : State {
    val isPromptValid = prompt.isNotBlank() && prompt.length >= 3
}

sealed interface BartenderAction : Action {
    data class OnPromptChanged(val prompt: String) : BartenderAction
    object OnGenerateDrinkClicked : BartenderAction
    object OnCloseClicked : BartenderAction
}

class BartenderStateMachine(
    drinkRepository: DrinkRepository,
    router: Router,
) : StateMachine<BartenderState, BartenderAction, NoSideEffect>(
    initialState = BartenderState(),
    reducer = { state, _ ->
        merge(
            filterIsInstance<BartenderAction.OnPromptChanged>()
                .map { state().copy(prompt = it.prompt) },
            filterIsInstance<BartenderAction.OnGenerateDrinkClicked>()
                .flatMapLatest {
                    drinkRepository.drinkForPrompt(state().prompt)
                        .take(1)
                        .map { state().copy(isLoading = false, isError = false) }
                        .onStart { emit(state().copy(isLoading = true, isError = false)) }
                        .catch { emit(state().copy(isError = true, isLoading = false)) }
                        .onCompletion { router.navigateBack() }
                },
            filterIsInstance<BartenderAction.OnCloseClicked>()
                .onEach { router.navigateBack() }
                .map { state() },
        )
    }
)
