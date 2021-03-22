package com.popalay.barnee.ui.screen.categorydrinks

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.model.Drink

data class CategoryDrinksState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class CategoryDrinksViewModel(initialState: CategoryDrinksState) : MavericksViewModel<CategoryDrinksState>(initialState) {
    fun loadDrinks(tag: String) {
        suspend { Api.drinksByTags(listOf(tag)) }.execute { copy(drinks = it) }
    }
}