package com.popalay.barnee.domain.similardrinks

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
import com.popalay.barnee.domain.similardrinks.SimilarDrinksAction.Initial
import com.popalay.barnee.domain.similardrinks.SimilarDrinksMutation.DrinksMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class SimilarDrinksState(
    val drinks: Result<List<Drink>> = Uninitialized()
) : State

sealed class SimilarDrinksAction : Action {
    data class Initial(val alias: String) : SimilarDrinksAction()
}

sealed class SimilarDrinksMutation : Mutation {
    data class DrinksMutation(val data: Result<List<Drink>>) : SimilarDrinksMutation()
}

class SimilarDrinksStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<SimilarDrinksState, SimilarDrinksAction, SimilarDrinksMutation>(SimilarDrinksState()) {
    override val processor: Processor<SimilarDrinksState, SimilarDrinksMutation> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getSimilarDrinksFor(it.alias) }
                .map { DrinksMutation(it) },
        )
    }

    override val reducer: Reducer<SimilarDrinksState, SimilarDrinksMutation> = { mutation ->
        when (mutation) {
            is DrinksMutation -> copy(drinks = mutation.data)
        }
    }
}