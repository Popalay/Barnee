package com.popalay.barnee.domain.favorites

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.favorites.FavoritesAction.Initial
import com.popalay.barnee.domain.favorites.FavoritesMutation.DrinksMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class FavoritesState(
    val drinks: Result<List<Drink>> = Uninitialized()
) : State

sealed class FavoritesAction : Action {
    object Initial : FavoritesAction()
}

sealed class FavoritesMutation : Mutation {
    data class DrinksMutation(val data: Result<List<Drink>>) : FavoritesMutation()
}

class FavoritesStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<FavoritesState, FavoritesAction, FavoritesMutation>(FavoritesState()) {
    override val processor: Processor<FavoritesState, FavoritesMutation> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFavoriteDrinks() }
                .map { DrinksMutation(it) },
        )
    }

    override val reducer: Reducer<FavoritesState, FavoritesMutation> = { mutation ->
        when (mutation) {
            is DrinksMutation -> copy(drinks = mutation.data)
        }
    }
}