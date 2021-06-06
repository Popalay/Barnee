package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
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
    data class DrinkWithRelated(val data: Result<FullDrinkResponse>) : DrinkMutation()
    data class TogglePlaying(val data: Boolean) : DrinkMutation()
}

class DrinkStateMachine(
    alias: String,
    drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction, DrinkMutation, Nothing>(
    initialState = DrinkState(),
    initialAction = DrinkAction.Initial(alias),
    processor = { state, _ ->
        merge(
            filterIsInstance<DrinkAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFullDrink(it.alias) }
                .map { DrinkMutation.DrinkWithRelated(it) },
            filterIsInstance<DrinkAction.Retry>()
                .flatMapToResult { drinkRepository.getFullDrink(alias) }
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