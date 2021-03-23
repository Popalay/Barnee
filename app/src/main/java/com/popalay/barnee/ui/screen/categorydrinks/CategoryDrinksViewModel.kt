package com.popalay.barnee.ui.screen.categorydrinks

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.remote.Api
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class CategoryDrinksState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class CategoryDrinksViewModel(initialState: CategoryDrinksState) : MavericksViewModel<CategoryDrinksState>(initialState), KoinComponent {
    private val api: Api by inject()

    fun loadDrinks(tag: String) {
        suspend { api.drinksByTags(listOf(tag)) }.execute { copy(drinks = it) }
    }
}