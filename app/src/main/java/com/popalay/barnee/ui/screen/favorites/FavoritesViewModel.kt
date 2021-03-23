package com.popalay.barnee.ui.screen.favorites

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class FavoritesState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class FavoritesViewModel(initialState: FavoritesState) : MavericksViewModel<FavoritesState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()

    init {
        loadDrinks()
    }

    private fun loadDrinks() {
        drinkRepository.getFavoriteDrinks()
            .execute { copy(drinks = it) }
    }
}