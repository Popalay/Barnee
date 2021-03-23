package com.popalay.barnee.ui.screen.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SearchState(
    val searchQuery: String = "",
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class SearchViewModel(initialState: SearchState) : MavericksViewModel<SearchState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()
    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        setState { copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = suspend {
            delay(500)
            drinkRepository.searchDrinks(query)
        }.execute { copy(drinks = it) }
    }
}