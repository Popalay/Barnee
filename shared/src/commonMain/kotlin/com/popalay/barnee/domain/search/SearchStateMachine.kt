package com.popalay.barnee.domain.search

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
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
import com.popalay.barnee.domain.search.SearchMutation.AggregationMutation
import com.popalay.barnee.domain.search.SearchMutation.FilterClickedMutation
import com.popalay.barnee.domain.search.SearchMutation.QueryChangedMutation
import com.popalay.barnee.domain.search.SearchMutation.SearchingMutation
import com.popalay.barnee.domain.search.SearchMutation.ShowFiltersMutation
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

sealed class SearchMutation : Mutation {
    data class AggregationMutation(val data: Result<Aggregation>) : SearchMutation()
    data class SearchingMutation(val data: Result<List<Drink>>) : SearchMutation()
    data class QueryChangedMutation(val data: String) : SearchMutation()
    data class FilterClickedMutation(val data: Set<Pair<String, AggregationGroup>>) : SearchMutation()
    data class ShowFiltersMutation(val data: Boolean) : SearchMutation()
}

class SearchStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<SearchState, SearchAction, SearchMutation>(SearchState()) {
    override val processor: Processor<SearchState, SearchMutation> = { state ->
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getAggregation() }
                .map { AggregationMutation(it) },
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.searchDrinks("", emptyMap()) }
                .map { SearchingMutation(it) },
            filterIsInstance<QueryChanged>()
                .map { QueryChangedMutation(it.query) },
            filterIsInstance<QueryChanged>()
                .debounce(500L)
                .distinctUntilChanged()
                .flatMapToResult {
                    drinkRepository.searchDrinks(
                        it.query,
                        getFilters(state().aggregation(), state().selectedFilters)
                    )
                }
                .map { SearchingMutation(it) },
            filterIsInstance<FilterClicked>()
                .map {
                    FilterClickedMutation(
                        if (it.value in state().selectedFilters) {
                            state().selectedFilters - it.value
                        } else {
                            state().selectedFilters + it.value
                        }
                    )
                },
            filterIsInstance<ShowFiltersClicked>()
                .map { ShowFiltersMutation(true) },
            filterIsInstance<FiltersDismissed>()
                .filter { state().let { it.appliedFilters != it.selectedFilters } }
                .flatMapToResult {
                    drinkRepository.searchDrinks(
                        state().searchQuery,
                        getFilters(state().aggregation(), state().selectedFilters)
                    )
                }
                .map { SearchingMutation(it) },
            filterIsInstance<FiltersDismissed>()
                .filter { state().let { it.appliedFilters == it.selectedFilters } }
                .map { ShowFiltersMutation(false) }
        )
    }

    override val reducer: Reducer<SearchState, SearchMutation> = { mutation ->
        when (mutation) {
            is AggregationMutation -> copy(aggregation = mutation.data)
            is SearchingMutation -> copy(drinks = mutation.data, appliedFilters = selectedFilters, isFiltersShown = false)
            is QueryChangedMutation -> copy(searchQuery = mutation.data)
            is FilterClickedMutation -> copy(selectedFilters = mutation.data)
            is ShowFiltersMutation -> copy(isFiltersShown = mutation.data)
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