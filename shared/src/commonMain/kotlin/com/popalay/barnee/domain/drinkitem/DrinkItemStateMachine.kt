package com.popalay.barnee.domain.drinkitem

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.drinkitem.DrinkItemAction.ToggleFavorite
import com.popalay.barnee.domain.drinkitem.DrinkItemMutation.ToggleFavoriteMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class DrinkItemState(
    val isFavorite: Boolean = false,
    val drinkAddedToCollection: Drink? = null
) : State

sealed class DrinkItemAction : Action {
    data class ToggleFavorite(val drink: Drink) : DrinkItemAction()
}

sealed class DrinkItemMutation : Mutation {
    data class ToggleFavoriteMutation(val data: Pair<Drink, Boolean>) : DrinkItemMutation()
}

class DrinkItemStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkItemState, DrinkItemAction, DrinkItemMutation>(DrinkItemState()) {
    override val processor: Processor<DrinkItemState, DrinkItemMutation> = {
        merge(
            filterIsInstance<ToggleFavorite>()
                .map { it.drink to drinkRepository.toggleFavoriteFor(it.drink.alias) }
                .map { ToggleFavoriteMutation(it) },
        )
    }

    override val reducer: Reducer<DrinkItemState, DrinkItemMutation> = { mutation ->
        when (mutation) {
            is ToggleFavoriteMutation -> copy(
                isFavorite = mutation.data.second,
                drinkAddedToCollection = if (mutation.data.second) mutation.data.first else null
            )
        }
    }
}