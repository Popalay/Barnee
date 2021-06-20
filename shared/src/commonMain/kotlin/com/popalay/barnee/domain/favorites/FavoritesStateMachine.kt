package com.popalay.barnee.domain.favorites

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class FavoritesState(
    val drinks: Flow<PagingData<Drink>> = emptyFlow()
) : State

sealed interface FavoritesAction : Action {
    object Initial : FavoritesAction
}

sealed interface FavoritesMutation : Mutation {
    data class Drinks(val data: Flow<PagingData<Drink>>) : FavoritesMutation
}

class FavoritesStateMachine(
    drinkRepository: DrinkRepository
) : StateMachine<FavoritesState, FavoritesAction, FavoritesMutation, Nothing>(
    initialState = FavoritesState(),
    initialAction = FavoritesAction.Initial,
    processor = { _, _ ->
        merge(
            filterIsInstance<FavoritesAction.Initial>()
                .take(1)
                .map { drinkRepository.drinks(DrinksRequest.Favorites) }
                .map { FavoritesMutation.Drinks(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is FavoritesMutation.Drinks -> copy(drinks = mutation.data)
        }
    }
)