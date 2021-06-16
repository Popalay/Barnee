package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Input
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
}

sealed interface DrinkMutation : Mutation {
    data class DrinkWithRelated(val data: Result<FullDrinkResponse>) : DrinkMutation
    data class TogglePlaying(val data: Boolean) : DrinkMutation
}

class DrinkStateMachine(
    input: DrinkInput,
    drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction, DrinkMutation, EmptySideEffect>(
    initialState = DrinkState(input),
    initialAction = DrinkAction.Initial,
    processor = { state, _ ->
        merge(
            filterIsInstance<DrinkAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFullDrink(state().alias) }
                .map { DrinkMutation.DrinkWithRelated(it) },
            filterIsInstance<DrinkAction.Retry>()
                .flatMapToResult { drinkRepository.getFullDrink(state().alias) }
                .map { DrinkMutation.DrinkWithRelated(it) },
            filterIsInstance<DrinkAction.TogglePlaying>()
                .map { !state().isPlaying }
                .map { DrinkMutation.TogglePlaying(it) },
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DrinkMutation.DrinkWithRelated -> copy(drinkWithRelated = mutation.data)
            is DrinkMutation.TogglePlaying -> copy(isPlaying = mutation.data)
        }
    }
)