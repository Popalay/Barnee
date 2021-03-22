package com.popalay.barnee.ui.screen.drink

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.Api
import com.popalay.barnee.data.LocalStore
import com.popalay.barnee.data.model.Receipt
import kotlinx.coroutines.flow.first

data class DrinkState(
    val receipt: Async<Receipt> = Uninitialized,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false
) : MavericksState

class DrinkViewModel(initialState: DrinkState) : MavericksViewModel<DrinkState>(initialState) {
    fun loadReceipt(alias: String) {
        suspend { Api.getReceipt(alias) }.execute { copy(receipt = it) }
        suspend { LocalStore.getFavoriteDrinks().first() }.execute { copy(isFavorite = alias in it.invoke() ?: emptySet()) }
    }

    fun toggleFavorite(alias: String) {
        withState { state ->
            suspend { if (state.isFavorite) LocalStore.removeFavorite(alias) else LocalStore.saveFavorite(alias) }
                .execute { copy(isFavorite = !state.isFavorite) }
        }
    }
}