package com.popalay.barnee.ui.screen.favorites

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Drink
import kotlinx.coroutines.flow.map

data class FavoritesState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class FavoritesViewModel(initialState: FavoritesState) : MavericksViewModel<FavoritesState>(initialState) {
    init {
        loadDrinks()
    }

    private fun loadDrinks() {
        LocalStore.getFavoriteDrinks()
            .map { Api.drinksByAliases(it.toList()) }
            .execute { copy(drinks = it) }
    }
}