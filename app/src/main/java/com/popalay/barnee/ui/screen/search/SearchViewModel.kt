package com.popalay.barnee.ui.screen.search

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Uninitialized
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SearchState(
    val searchQuery: String = "",
    val drinks: Async<List<Drink>> = Uninitialized,
    val aggregation: Async<Aggregation> = Uninitialized,
    val selectedGroups: Set<Pair<String, AggregationGroup>> = emptySet(),
    val isBackDropRevealed: Boolean = false
) : MavericksState

class SearchViewModel(initialState: SearchState) : MavericksViewModel<SearchState>(initialState), KoinComponent {
    private val drinkRepository by inject<DrinkRepository>()
    private var searchJob: Job? = null

    init {
        loadAggregation()
    }

    fun onSearchQueryChanged(query: String) {
        setState { copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = suspend {
            delay(500)
            requestSearch()
        }.execute { copy(drinks = it) }
    }

    fun onAggregationGroupClicked(value: Pair<String, AggregationGroup>) {
        setState {
            if (value in selectedGroups) {
                copy(selectedGroups = selectedGroups - value)
            } else {
                copy(selectedGroups = selectedGroups + value)
            }
        }
    }

    fun onApplyClicked() {
        setState { copy(isBackDropRevealed = true) }
        suspend { requestSearch() }.execute { copy(drinks = it) }
    }

    private suspend fun requestSearch() = awaitState().run {
        drinkRepository.searchDrinks(searchQuery, getFilters(aggregation(), selectedGroups))
    }

    private fun getFilters(
        aggregation: Aggregation?,
        selectedGroups: Set<Pair<String, AggregationGroup>>,
    ): Map<String, List<String>> =
        selectedGroups.map {
            val aggregationName = when (it.second) {
                aggregation?.tasting -> "tasting"
                aggregation?.skill -> "skill"
                aggregation?.servedIn -> "servedIn"
                aggregation?.colored -> "colored"
                aggregation?.withType -> "withtype"
                else -> ""
            }
            aggregationName to it.first
        }.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )

    private fun loadAggregation() {
        suspend { drinkRepository.getAggregation() }.execute { copy(aggregation = it) }
    }
}