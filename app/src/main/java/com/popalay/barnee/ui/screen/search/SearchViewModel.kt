package com.popalay.barnee.ui.screen.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.model.Drink
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class SearchState(
    val searchQuery: String = "",
    val drinks: Async<List<Drink>> = Uninitialized,
) : MavericksState

class SearchViewModel(initialState: SearchState) : MavericksViewModel<SearchState>(initialState) {
    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        setState { copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = suspend {
            delay(500)
            Api.searchDrinks(query)
        }.execute { copy(drinks = it) }
    }
}