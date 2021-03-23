package com.popalay.barnee.ui.screen.drink

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Receipt
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class DrinkState(
    val receipt: Async<Receipt> = Uninitialized,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false
) : MavericksState

class DrinkViewModel(initialState: DrinkState) : MavericksViewModel<DrinkState>(initialState), KoinComponent {
    private val api: Api by inject()

    fun loadReceipt(alias: String) {
        suspend { api.getReceipt(alias) }.execute { copy(receipt = it) }
        suspend { LocalStore.getFavoriteDrinks().first() }.execute { copy(isFavorite = alias in it.invoke() ?: emptySet()) }
    }

    fun toggleFavorite(alias: String) {
        withState { state ->
            suspend { if (state.isFavorite) LocalStore.removeFavorite(alias) else LocalStore.saveFavorite(alias) }
                .execute { copy(isFavorite = !state.isFavorite) }
        }
    }
}