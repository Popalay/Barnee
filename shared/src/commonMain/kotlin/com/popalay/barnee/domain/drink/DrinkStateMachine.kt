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

package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.ShareRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.SideEffect
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.SimilarDrinksDestination
import com.popalay.barnee.domain.navigation.TagDrinksDestination
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

data class DrinkInput(
    val alias: String,
    val name: String,
    val image: ImageUrl
) : Input

data class DrinkState(
    val alias: String,
    val name: String,
    val image: ImageUrl,
    val drinkWithRelated: Result<FullDrinkResponse> = Uninitialized(),
    val isScreenKeptOn: Boolean = false,
    val isPlaying: Boolean = false
) : State {
    val displayName = drinkWithRelated()?.drink?.displayName ?: name
    val displayImage = drinkWithRelated()?.drink?.displayImageUrl ?: image

    constructor(input: DrinkInput) : this(input.alias, input.name, input.image)
}

sealed interface DrinkAction : Action {
    object Initial : DrinkAction
    object TogglePlaying : DrinkAction
    object Retry : DrinkAction
    object MoreRecommendedDrinksClicked : DrinkAction
    object ShareClicked : DrinkAction
    object KeepScreenOnClicked : DrinkAction
    data class CategoryClicked(val category: Category) : DrinkAction
}

sealed interface DrinkMutation : Mutation {
    object Nothing : DrinkMutation
    data class DrinkWithRelated(val data: Result<FullDrinkResponse>) : DrinkMutation
    data class TogglePlaying(val data: Boolean) : DrinkMutation
    data class KeepScreenOn(val data: Boolean) : DrinkMutation
}

sealed interface DrinkSideEffect : SideEffect {
    data class KeepScreenOn(val keep: Boolean) : DrinkSideEffect
}

class DrinkStateMachine(
    input: DrinkInput,
    drinkRepository: DrinkRepository,
    shareRepository: ShareRepository,
    router: Router
) : StateMachine<DrinkState, DrinkAction, DrinkMutation, DrinkSideEffect>(
    initialState = DrinkState(input),
    initialAction = DrinkAction.Initial,
    processor = { state, sideEffectConsumer ->
        merge(
            filterIsInstance<DrinkAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.fullDrink(state().alias) }
                .map { DrinkMutation.DrinkWithRelated(it) },
            filterIsInstance<DrinkAction.Retry>()
                .flatMapToResult { drinkRepository.fullDrink(state().alias) }
                .map { DrinkMutation.DrinkWithRelated(it) },
            filterIsInstance<DrinkAction.TogglePlaying>()
                .map { !state().isPlaying }
                .map { DrinkMutation.TogglePlaying(it) },
            filterIsInstance<DrinkAction.MoreRecommendedDrinksClicked>()
                .onEach { router.navigate(SimilarDrinksDestination(state().alias, state().displayName)) }
                .map { DrinkMutation.Nothing },
            filterIsInstance<DrinkAction.CategoryClicked>()
                .onEach { router.navigate(TagDrinksDestination(it.category)) }
                .map { DrinkMutation.Nothing },
            filterIsInstance<DrinkAction.ShareClicked>()
                .onEach { shareRepository.shareDrink(requireNotNull(state().drinkWithRelated()?.drink)) }
                .map { DrinkMutation.Nothing },
            filterIsInstance<DrinkAction.KeepScreenOnClicked>()
                .map { !state().isScreenKeptOn }
                .onEach { sideEffectConsumer(DrinkSideEffect.KeepScreenOn(it)) }
                .map { DrinkMutation.KeepScreenOn(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DrinkMutation.DrinkWithRelated -> copy(drinkWithRelated = mutation.data)
            is DrinkMutation.TogglePlaying -> copy(isPlaying = mutation.data)
            is DrinkMutation.KeepScreenOn -> copy(isScreenKeptOn = mutation.data)
            is DrinkMutation.Nothing -> this
        }
    }
)
