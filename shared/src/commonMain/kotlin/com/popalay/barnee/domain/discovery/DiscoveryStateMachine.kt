package com.popalay.barnee.domain.discovery

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
import com.popalay.barnee.domain.discovery.DiscoveryAction.Initial
import com.popalay.barnee.domain.discovery.DiscoveryOutput.DrinksOutput
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class DiscoveryState(
    val drinks: Result<List<Drink>> = Uninitialized()
) : State

sealed class DiscoveryAction : Action {
    object Initial : DiscoveryAction()
}

sealed class DiscoveryOutput : Output {
    data class DrinksOutput(val data: Result<List<Drink>>) : DiscoveryOutput()
}

class DiscoveryStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DiscoveryState, DiscoveryAction, DiscoveryOutput>(DiscoveryState()) {
    override val processor: Processor<DiscoveryState, DiscoveryOutput> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getRandomDrinks(10) }
                .map { DrinksOutput(it) }
        )
    }

    override val reducer: Reducer<DiscoveryState, DiscoveryOutput> = { result ->
        when (result) {
            is DrinksOutput -> copy(drinks = result.data)
        }
    }
}