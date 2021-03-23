package com.popalay.barnee.ui.screen.categorydrinks

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class CategoryDrinksState(
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class CategoryDrinksViewModel(initialState: CategoryDrinksState) : MavericksViewModel<CategoryDrinksState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()

    fun loadDrinks(tag: String) {
        suspend { drinkRepository.getDrinksByTags(listOf(tag)) }.execute { copy(drinks = it) }
    }
}