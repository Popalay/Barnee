package com.popalay.barnee.domain.drinkitem

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class DrinkItemState(
    val isFavorite: Boolean = false
) : State

sealed interface DrinkItemAction : Action {
    data class ToggleFavorite(val drink: Drink) : DrinkItemAction
}

sealed interface DrinkItemMutation : Mutation {
    data class ToggleFavorite(val data: Boolean) : DrinkItemMutation
}

class DrinkItemStateMachine(
    drinkRepository: DrinkRepository
) : StateMachine<DrinkItemState, DrinkItemAction, DrinkItemMutation, EmptySideEffect>(
    initialState = DrinkItemState(),
    processor = { _, _ ->
        merge(
            filterIsInstance<DrinkItemAction.ToggleFavorite>()
                .map { drinkRepository.toggleFavoriteFor(it.drink.alias) }
                .map { DrinkItemMutation.ToggleFavorite(it) },
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DrinkItemMutation.ToggleFavorite -> copy(isFavorite = mutation.data)
        }
    }
)
