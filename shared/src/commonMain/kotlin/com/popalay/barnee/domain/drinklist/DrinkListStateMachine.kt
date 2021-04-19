package com.popalay.barnee.domain.drinklist

import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.drinklist.DrinkListAction.ToggleFavorite
import com.popalay.barnee.domain.drinklist.DrinkListMutation.ToggleFavoriteMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class DrinkListState(
    val isFavorite: Boolean = false
) : State

sealed class DrinkListAction : Action {
    data class ToggleFavorite(val alias: String) : DrinkListAction()
}

sealed class DrinkListMutation : Mutation {
    data class ToggleFavoriteMutation(val data: Boolean) : DrinkListMutation()
}

class DrinkListStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkListState, DrinkListAction, DrinkListMutation>(DrinkListState()) {
    override val processor: Processor<DrinkListState, DrinkListMutation> = {
        merge(
            filterIsInstance<ToggleFavorite>()
                .map { drinkRepository.toggleFavoriteFor(it.alias) }
                .map { ToggleFavoriteMutation(it) },
        )
    }

    override val reducer: Reducer<DrinkListState, DrinkListMutation> = { mutation ->
        when (mutation) {
            is ToggleFavoriteMutation -> copy(isFavorite = mutation.data)
        }
    }
}