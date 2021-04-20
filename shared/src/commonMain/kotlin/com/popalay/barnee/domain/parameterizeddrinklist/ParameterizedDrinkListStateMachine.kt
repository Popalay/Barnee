package com.popalay.barnee.domain.parameterizeddrinklist

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListAction.Initial
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListMutation.DrinksMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class ParameterizedDrinkListState(
    val drinks: Result<List<Drink>> = Uninitialized()
) : State

sealed class ParameterizedDrinkListAction : Action {
    data class Initial(val request: DrinksRequest) : ParameterizedDrinkListAction()
}

sealed class ParameterizedDrinkListMutation : Mutation {
    data class DrinksMutation(val data: Result<List<Drink>>) : ParameterizedDrinkListMutation()
}

class ParameterizedDrinkListStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<ParameterizedDrinkListState, ParameterizedDrinkListAction, ParameterizedDrinkListMutation>(
    ParameterizedDrinkListState()
) {
    override val processor: Processor<ParameterizedDrinkListState, ParameterizedDrinkListMutation> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getDrinks(it.request) }
                .map { DrinksMutation(it) },
        )
    }

    override val reducer: Reducer<ParameterizedDrinkListState, ParameterizedDrinkListMutation> = { mutation ->
        when (mutation) {
            is DrinksMutation -> copy(drinks = mutation.data)
        }
    }
}