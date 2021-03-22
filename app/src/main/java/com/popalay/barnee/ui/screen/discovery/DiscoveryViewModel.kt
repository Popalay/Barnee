package com.popalay.barnee.ui.screen.discovery

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.Api
import com.popalay.barnee.data.model.Drink

data class DiscoveryState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class DiscoveryViewModel(initialState: DiscoveryState) : MavericksViewModel<DiscoveryState>(initialState) {

    init {
        loadDrinks()
    }

    private fun loadDrinks() {
        suspend { Api.randomDrinks(10) }.execute { copy(drinks = it) }
    }
}