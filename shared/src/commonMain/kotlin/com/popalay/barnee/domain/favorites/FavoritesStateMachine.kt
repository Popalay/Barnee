package com.popalay.barnee.domain.favorites

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.favorites.FavoritesAction.Initial

data class FavoritesState(
    val drinks: Result<List<Drink>> = Uninitialized()
) : State

sealed class FavoritesAction : Action {
    object Initial : FavoritesAction()
}

class FavoritesStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<FavoritesState, FavoritesAction>(FavoritesState()) {
    override fun reducer(currentState: FavoritesState, action: FavoritesAction) {
        when (action) {
            is Initial -> drinkRepository.getFavoriteDrinks().execute { copy(drinks = it) }
        }
    }
}