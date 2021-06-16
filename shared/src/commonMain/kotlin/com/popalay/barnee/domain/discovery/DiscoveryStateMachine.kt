package com.popalay.barnee.domain.discovery

import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
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

data class DiscoveryState(
    val categories: Result<List<Category>> = Uninitialized()
) : State

sealed interface DiscoveryAction : Action {
    object Initial : DiscoveryAction
}

sealed interface DiscoveryMutation : Mutation {
    data class Categories(val data: Result<List<Category>>) : DiscoveryMutation
}

class DiscoveryStateMachine(
    drinkRepository: DrinkRepository,
) : StateMachine<DiscoveryState, DiscoveryAction, DiscoveryMutation, EmptySideEffect>(
    initialState = DiscoveryState(),
    initialAction = DiscoveryAction.Initial,
    processor = { _, _ ->
        merge(
            filterIsInstance<DiscoveryAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getCategories() }
                .map { DiscoveryMutation.Categories(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is DiscoveryMutation.Categories -> copy(categories = mutation.data)
        }
    }
)