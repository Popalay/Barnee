package com.popalay.barnee.domain.discovery

import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.discovery.DiscoveryAction.Initial
import com.popalay.barnee.domain.discovery.DiscoveryMutation.CategoriesMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class DiscoveryState(
    val categories: Result<List<Category>> = Uninitialized()
) : State

sealed class DiscoveryAction : Action {
    object Initial : DiscoveryAction()
}

sealed class DiscoveryMutation : Mutation {
    data class CategoriesMutation(val data: Result<List<Category>>) : DiscoveryMutation()
}

class DiscoveryStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DiscoveryState, DiscoveryAction, DiscoveryMutation>(DiscoveryState()) {
    override val processor: Processor<DiscoveryState, DiscoveryMutation> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getCategories() }
                .map { CategoriesMutation(it) }
        )
    }

    override val reducer: Reducer<DiscoveryState, DiscoveryMutation> = { mutation ->
        when (mutation) {
            is CategoriesMutation -> copy(categories = mutation.data)
        }
    }
}