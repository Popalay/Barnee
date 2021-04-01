package com.popalay.barnee.domain.similardrinks

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
import com.popalay.barnee.domain.similardrinks.SimilarDrinksAction.Initial
import com.popalay.barnee.domain.similardrinks.SimilarDrinksOutput.DrinksOutput
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

sealed class SimilarDrinksOutput : Output {
    data class DrinksOutput(val data: Result<List<Drink>>) : SimilarDrinksOutput()
}

class SimilarDrinksStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<SimilarDrinksState, SimilarDrinksAction, SimilarDrinksOutput>(SimilarDrinksState()) {
    override val processor: Processor<SimilarDrinksState, SimilarDrinksOutput> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getSimilarDrinksFor(it.alias) }
                .map { DrinksOutput(it) },
        )
    }

    override val reducer: Reducer<SimilarDrinksState, SimilarDrinksOutput> = { result ->
        when (result) {
            is DrinksOutput -> copy(drinks = result.data)
        }
    }
}