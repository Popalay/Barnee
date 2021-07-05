package com.popalay.barnee.domain.drinkitem

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

object DrinkItemState : State

sealed interface DrinkItemAction : Action {
    data class ToggleFavorite(val drink: Drink) : DrinkItemAction
}

sealed interface DrinkItemMutation : Mutation {
    object Nothing : DrinkItemMutation
}

class DrinkItemStateMachine(
    collectionRepository: CollectionRepository
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
                .map { DrinkItemMutation.Nothing }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DrinkItemMutation.Nothing -> this
        }
    }
)
