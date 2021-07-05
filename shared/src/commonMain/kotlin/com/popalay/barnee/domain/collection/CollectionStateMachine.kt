package com.popalay.barnee.domain.collection

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

data class CollectionInput(val name: String) : Input

data class CollectionState(
    val name: String,
    val drinks: Flow<PagingData<Drink>> = emptyFlow()
) : State {
    constructor(input: CollectionInput) : this(input.name)
}

sealed interface CollectionAction : Action {
    object Initial : CollectionAction
}

sealed interface CollectionMutation : Mutation {
    data class Drinks(val data: Flow<PagingData<Drink>>) : CollectionMutation
}

class CollectionStateMachine(
    input: CollectionInput,
    drinkRepository: DrinkRepository
) : StateMachine<CollectionState, CollectionAction, CollectionMutation, EmptySideEffect>(
    initialState = CollectionState(input),
    initialAction = CollectionAction.Initial,
    processor = { state, _ ->
        merge(
            filterIsInstance<CollectionAction.Initial>()
                .take(1)
                .map { drinkRepository.drinks(DrinksRequest.Collection(state().name)) }
                .map { CollectionMutation.Drinks(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is CollectionMutation.Drinks -> copy(drinks = mutation.data)
        }
    }
)
