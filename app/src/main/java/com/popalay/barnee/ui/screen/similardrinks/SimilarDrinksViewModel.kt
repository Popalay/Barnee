package com.popalay.barnee.ui.screen.similardrinks

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SimilarDrinksState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class SimilarDrinksViewModel(initialState: SimilarDrinksState) : MavericksViewModel<SimilarDrinksState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()

    fun loadDrinks(alias: String) {
        suspend { drinkRepository.getSimilarDrinksFor(alias) }.execute { copy(drinks = it) }
    }
}