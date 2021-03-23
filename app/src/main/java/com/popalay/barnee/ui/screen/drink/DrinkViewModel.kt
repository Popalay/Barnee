package com.popalay.barnee.ui.screen.drink

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Receipt
import com.popalay.barnee.data.repository.DrinkRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class DrinkState(
    val receipt: Async<Receipt> = Uninitialized,
    val isLoading: Boolean = false
) : MavericksState

class DrinkViewModel(initialState: DrinkState) : MavericksViewModel<DrinkState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()

    fun loadReceipt(alias: String) {
        drinkRepository.getReceipt(alias).execute { copy(receipt = it) }
    }

    fun toggleFavorite(alias: String) {
        suspend { drinkRepository.toggleFavoriteFor(alias) }.execute { this }
    }
}