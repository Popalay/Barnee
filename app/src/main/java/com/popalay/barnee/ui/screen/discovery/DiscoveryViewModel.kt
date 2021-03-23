package com.popalay.barnee.ui.screen.discovery

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class DiscoveryState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class DiscoveryViewModel(initialState: DiscoveryState) : MavericksViewModel<DiscoveryState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()

    init {
        loadDrinks()
    }

    private fun loadDrinks() {
        suspend { drinkRepository.getRandomDrinks(10) }.execute { copy(drinks = it) }
    }
}