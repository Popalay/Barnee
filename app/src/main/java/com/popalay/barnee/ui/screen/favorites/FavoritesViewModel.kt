package com.popalay.barnee.ui.screen.favorites

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Drink
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class FavoritesState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class FavoritesViewModel(initialState: FavoritesState) : MavericksViewModel<FavoritesState>(initialState), KoinComponent {
    private val api: Api by inject()

    init {
        loadDrinks()
    }

    private fun loadDrinks() {
        LocalStore.getFavoriteDrinks()
            .map { api.drinksByAliases(it.toList()) }
            .execute { copy(drinks = it) }
    }
}