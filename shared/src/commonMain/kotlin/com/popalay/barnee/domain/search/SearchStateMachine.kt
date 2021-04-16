package com.popalay.barnee.domain.search

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Output
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Success
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.search.SearchAction.FilterClicked
import com.popalay.barnee.domain.search.SearchAction.FiltersDismissed
import com.popalay.barnee.domain.search.SearchAction.Initial
import com.popalay.barnee.domain.search.SearchAction.QueryChanged
import com.popalay.barnee.domain.search.SearchAction.ShowFiltersClicked
import com.popalay.barnee.domain.search.SearchOutput.AggregationOutput
import com.popalay.barnee.domain.search.SearchOutput.FilterClickedOutput
import com.popalay.barnee.domain.search.SearchOutput.QueryChangedOutput
import com.popalay.barnee.domain.search.SearchOutput.SearchingOutput
import com.popalay.barnee.domain.search.SearchOutput.ShowFiltersOutput
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class SearchState(
    val searchQuery: String = "",
    val drinks: Result<List<Drink>> = Success(emptyList()),
    val aggregation: Result<Aggregation> = Uninitialized(),
    val selectedFilters: Set<Pair<String, AggregationGroup>> = emptySet(),
    val appliedFilters: Set<Pair<String, AggregationGroup>> = emptySet(),
    val isFiltersShown: Boolean = false
) : State

sealed class SearchAction : Action {
    object Initial : SearchAction()
    object ShowFiltersClicked : SearchAction()
    object FiltersDismissed : SearchAction()
    data class QueryChanged(val query: String) : SearchAction()
    data class FilterClicked(val value: Pair<String, AggregationGroup>) : SearchAction()
}

sealed class SearchOutput : Output {
    data class AggregationOutput(val data: Aggregation) : SearchOutput()
    data class SearchingOutput(val data: Result<List<Drink>>) : SearchOutput()
    data class QueryChangedOutput(val data: String) : SearchOutput()
    data class FilterClickedOutput(val data: Set<Pair<String, AggregationGroup>>) : SearchOutput()
    data class ShowFiltersOutput(val data: Boolean) : SearchOutput()
}

class SearchStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<SearchState, SearchAction, SearchOutput>(SearchState()) {
    override val processor: Processor<SearchState, SearchOutput> = { state ->
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .map { drinkRepository.getAggregation() }
                .map { AggregationOutput(it) },
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.searchDrinks("", emptyMap()) }
                .map { SearchingOutput(it) },
            filterIsInstance<QueryChanged>()
                .map { QueryChangedOutput(it.query) },
            filterIsInstance<QueryChanged>()
                .debounce(500L)
                .distinctUntilChanged()
                .flatMapToResult {
                    drinkRepository.searchDrinks(
                        it.query,
                        getFilters(state().aggregation(), state().selectedFilters)
                    )
                }
                .map { SearchingOutput(it) },
            filterIsInstance<FilterClicked>()
                .map {
                    FilterClickedOutput(
                        if (it.value in state().selectedFilters) {
                            state().selectedFilters - it.value
                        } else {
                            state().selectedFilters + it.value
                        }
                    )
                },
            filterIsInstance<ShowFiltersClicked>()
                .map { ShowFiltersOutput(true) },
            filterIsInstance<FiltersDismissed>()
                .filter { state().let { it.appliedFilters != it.selectedFilters } }
                .flatMapToResult {
                    drinkRepository.searchDrinks(
                        state().searchQuery,
                        getFilters(state().aggregation(), state().selectedFilters)
                    )
                }
                .map { SearchingOutput(it) },
            filterIsInstance<FiltersDismissed>()
                .filter { state().let { it.appliedFilters == it.selectedFilters } }
                .map { ShowFiltersOutput(false) }
        )
    }

    override val reducer: Reducer<SearchState, SearchOutput> = { result ->
        when (result) {
            is AggregationOutput -> copy(aggregation = Success(result.data))
            is SearchingOutput -> copy(drinks = result.data, appliedFilters = selectedFilters, isFiltersShown = false)
            is QueryChangedOutput -> copy(searchQuery = result.data)
            is FilterClickedOutput -> copy(selectedFilters = result.data)
            is ShowFiltersOutput -> copy(isFiltersShown = result.data)
        }
    }

    private fun getFilters(
        aggregation: Aggregation?,
        selectedGroups: Set<Pair<String, AggregationGroup>>,
    ): Map<String, List<String>> = selectedGroups.map {
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
}