package com.popalay.barnee.ui.screen.similardrinks

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.Api
import com.popalay.barnee.data.model.Drink

data class SimilarDrinksState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class SimilarDrinksViewModel(initialState: SimilarDrinksState) : MavericksViewModel<SimilarDrinksState>(initialState) {
    fun loadDrinks(alias: String) {
        suspend { Api.similarDrinks(alias) }.execute { copy(drinks = it) }
    }
}