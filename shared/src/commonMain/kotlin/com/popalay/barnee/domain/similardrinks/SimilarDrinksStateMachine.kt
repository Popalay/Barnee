package com.popalay.barnee.domain.similardrinks

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.similardrinks.SimilarDrinksAction.Initial

data class SimilarDrinksState(
    val drinks: Result<List<Drink>> = Uninitialized,
) : State

sealed class SimilarDrinksAction : Action {
    data class Initial(val alias: String) : SimilarDrinksAction()
}

class SimilarDrinksStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<SimilarDrinksState, SimilarDrinksAction>(SimilarDrinksState()) {
    override fun reducer(currentState: SimilarDrinksState, action: SimilarDrinksAction) {
        when (action) {
            is Initial -> suspend { drinkRepository.getSimilarDrinksFor(action.alias) }.execute { copy(drinks = it) }
        }
    }
}