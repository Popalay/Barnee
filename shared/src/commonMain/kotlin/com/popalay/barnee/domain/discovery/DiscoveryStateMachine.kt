package com.popalay.barnee.domain.discovery

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.discovery.DiscoveryAction.Initial

data class DiscoveryState(
    val drinks: Result<List<Drink>> = Uninitialized
) : State

sealed class DiscoveryAction : Action {
    object Initial : DiscoveryAction()
}

class DiscoveryStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DiscoveryState, DiscoveryAction>(DiscoveryState()) {
    override fun reducer(currentState: DiscoveryState, action: DiscoveryAction) {
        when (action) {
            Initial -> suspend { drinkRepository.getRandomDrinks(10) }.execute { copy(drinks = it) }
        }
    }
}