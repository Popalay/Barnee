package com.popalay.barnee.domain.parameterizeddrinklist

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
    val drinks: Flow<PagingData<Drink>> = emptyFlow()
) : State {
    constructor(input: ParameterizedDrinkListInput) : this(input.request, input.title, input.titleHighlighted)
}

sealed interface ParameterizedDrinkListAction : Action {
    object Initial : ParameterizedDrinkListAction
}

sealed interface ParameterizedDrinkListMutation : Mutation {
    data class Drinks(val data: Flow<PagingData<Drink>>) : ParameterizedDrinkListMutation
}

class ParameterizedDrinkListStateMachine(
    input: ParameterizedDrinkListInput,
    drinkRepository: DrinkRepository
) : StateMachine<ParameterizedDrinkListState, ParameterizedDrinkListAction, ParameterizedDrinkListMutation, EmptySideEffect>(
    initialState = ParameterizedDrinkListState(input),
    initialAction = ParameterizedDrinkListAction.Initial,
    processor = { state, _ ->
        merge(
            filterIsInstance<ParameterizedDrinkListAction.Initial>()
                .take(1)
                .map { drinkRepository.drinks(state().request) }
                .map { ParameterizedDrinkListMutation.Drinks(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is ParameterizedDrinkListMutation.Drinks -> copy(drinks = mutation.data)
        }
    }
)
