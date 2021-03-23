package com.popalay.barnee.ui.screen.discovery

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.remote.Api
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class DiscoveryState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class DiscoveryViewModel(initialState: DiscoveryState) : MavericksViewModel<DiscoveryState>(initialState), KoinComponent {
    private val api: Api by inject()

    init {
        loadDrinks()
    }

    private fun loadDrinks() {
        suspend { api.randomDrinks(10) }.execute { copy(drinks = it) }
    }
}