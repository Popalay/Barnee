package com.popalay.barnee.domain.favorites

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Output
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.favorites.FavoritesAction.Initial
import com.popalay.barnee.domain.favorites.FavoritesOutput.DrinksOutput
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

sealed class FavoritesOutput : Output {
    data class DrinksOutput(val data: Result<List<Drink>>) : FavoritesOutput()
}

class FavoritesStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<FavoritesState, FavoritesAction, FavoritesOutput>(FavoritesState()) {
    override val processor: Processor<FavoritesState, FavoritesOutput> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFavoriteDrinks() }
                .map { DrinksOutput(it) },
        )
    }

    override val reducer: Reducer<FavoritesState, FavoritesOutput> = { result ->
        when (result) {
            is DrinksOutput -> copy(drinks = result.data)
        }
    }
}