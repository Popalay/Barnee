package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.drink.DrinkAction.Initial
import com.popalay.barnee.domain.drink.DrinkAction.Retry
import com.popalay.barnee.domain.drink.DrinkAction.TogglePlaying
import com.popalay.barnee.domain.drink.DrinkMutation.DrinkWithRelatedMutation
import com.popalay.barnee.domain.drink.DrinkMutation.TogglePlayingMutation
import com.popalay.barnee.domain.flatMapToResult
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class DrinkState(
    val drinkWithRelated: Result<FullDrinkResponse> = Uninitialized(),
    val isPlaying: Boolean = false
) : State

sealed class DrinkAction : Action {
    data class Initial(val alias: String) : DrinkAction()
    object TogglePlaying : DrinkAction()
    object Retry : DrinkAction()
}

sealed class DrinkMutation : Mutation {
    data class DrinkWithRelatedMutation(val data: Result<FullDrinkResponse>) : DrinkMutation()
    data class TogglePlayingMutation(val data: Boolean) : DrinkMutation()
}

class DrinkStateMachine(
    alias: String,
    drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction, DrinkMutation>(
    initialState = DrinkState(),
    initialAction = Initial(alias),
    processor = { state ->
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFullDrink(it.alias) }
                .map { DrinkWithRelatedMutation(it) },
            filterIsInstance<Retry>()
                .flatMapToResult { drinkRepository.getFullDrink(alias) }
                .map { DrinkWithRelatedMutation(it) },
            filterIsInstance<TogglePlaying>()
                .map { !state().isPlaying }
                .map { TogglePlayingMutation(it) },
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DrinkWithRelatedMutation -> copy(drinkWithRelated = mutation.data)
            is TogglePlayingMutation -> copy(isPlaying = mutation.data)
        }
    }
)