package com.popalay.barnee.domain.parameterizeddrinklist

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class ParameterizedDrinkListInput(
    val request: DrinksRequest,
    val title: String,
    val titleHighlighted: String = ""
) : Input

data class ParameterizedDrinkListState(
    val request: DrinksRequest,
    val title: String,
    val titleHighlighted: String,
    val drinks: Result<List<Drink>> = Uninitialized()
) : State{
    constructor(input: ParameterizedDrinkListInput): this(input.request, input.title, input.titleHighlighted)
}

sealed interface ParameterizedDrinkListAction : Action {
    object Initial : ParameterizedDrinkListAction
    object Retry : ParameterizedDrinkListAction
}

sealed interface ParameterizedDrinkListMutation : Mutation {
    data class Drinks(val data: Result<List<Drink>>) : ParameterizedDrinkListMutation
}

class ParameterizedDrinkListStateMachine(
    input: ParameterizedDrinkListInput,
    drinkRepository: DrinkRepository
) : StateMachine<ParameterizedDrinkListState, ParameterizedDrinkListAction, ParameterizedDrinkListMutation, Nothing>(
    initialState = ParameterizedDrinkListState(input),
    initialAction = ParameterizedDrinkListAction.Initial,
    processor = { state, _ ->
        merge(
            filterIsInstance<ParameterizedDrinkListAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getDrinks(state().request) }
                .map { ParameterizedDrinkListMutation.Drinks(it) },
            filterIsInstance<ParameterizedDrinkListAction.Retry>()
                .flatMapToResult { drinkRepository.getDrinks(state().request) }
                .map { ParameterizedDrinkListMutation.Drinks(it) },
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is ParameterizedDrinkListMutation.Drinks -> copy(drinks = mutation.data)
        }
    }
)