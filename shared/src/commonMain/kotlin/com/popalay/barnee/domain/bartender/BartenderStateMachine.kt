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

package com.popalay.barnee.domain.bartender

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart

data class BartenderState(
    val generatedDrink: Drink? = null,
    val prompt: String = "",
    val isLoading: Boolean = false,
    val error: String = "",
) : State {
    val isPromptValid = prompt.isNotBlank() && prompt.length >= 3
    val isError = error.isNotBlank()
}

sealed interface BartenderAction : Action {
    data class OnPromptChanged(val prompt: String) : BartenderAction
    object OnGenerateDrinkClicked : BartenderAction
}

class BartenderStateMachine(
    drinkRepository: DrinkRepository,
) : StateMachine<BartenderState>(
    initialState = BartenderState(),
    reducer = { state, _ ->
        merge(
            filterIsInstance<BartenderAction.OnPromptChanged>()
                .map { state().copy(prompt = it.prompt.trimStart(), error = "") },
            filterIsInstance<BartenderAction.OnGenerateDrinkClicked>()
                .flatMapLatest {
                    drinkRepository.drinkForPrompt(state().prompt)
                        .map { state().copy(generatedDrink = it, isLoading = false, error = "") }
                        .onStart { emit(state().copy(isLoading = true, error = "")) }
                        .catch { emit(state().copy(error = "Something went wrong. Please try again.", isLoading = false)) }
                }
        )
    }
)
