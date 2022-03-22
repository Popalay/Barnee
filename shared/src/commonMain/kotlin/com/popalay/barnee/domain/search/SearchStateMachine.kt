/*
 * Copyright (c) 2022 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.domain.search

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Fail
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.SideEffect
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

data class SearchState(
    val searchQuery: String = "",
    val drinks: Flow<PagingData<Drink>> = emptyFlow(),
    val aggregation: Result<Aggregation> = Uninitialized(),
    val selectedFilters: Set<Pair<String, AggregationGroup>> = emptySet(),
    val appliedFilters: Set<Pair<String, AggregationGroup>> = emptySet(),
) : State

sealed interface SearchAction : Action {
    object Initial : SearchAction
    object Retry : SearchAction
    object ShowFiltersClicked : SearchAction
    object ClearSearchQuery : SearchAction
    object FiltersDismissed : SearchAction
    data class QueryChanged(val query: String) : SearchAction
    data class FilterClicked(val value: Pair<String, AggregationGroup>) : SearchAction
}

sealed interface SearchSideEffect : SideEffect {
    object ShowFilters : SearchSideEffect
}

class SearchStateMachine(
    drinkRepository: DrinkRepository,
) : StateMachine<SearchState, SearchAction, SearchSideEffect>(
    initialState = SearchState(),
    initialAction = SearchAction.Initial,
    reducer = { state, sideEffectConsumer ->
        merge(
            filterIsInstance<SearchAction.Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.aggregation() }
                .map { state().copy(aggregation = it) },
            filterIsInstance<SearchAction.Initial>()
                .take(1)
                .map { state().searchRequest(drinkRepository) }
                .map { state().copy(drinks = it, appliedFilters = state().selectedFilters) },
            filterIsInstance<SearchAction.Retry>()
                .filter { state().aggregation is Fail }
                .flatMapToResult { drinkRepository.aggregation() }
                .map { state().copy(aggregation = it) },
            filterIsInstance<SearchAction.QueryChanged>()
                .map { state().copy(searchQuery = it.query) },
            filterIsInstance<SearchAction.QueryChanged>()
                .debounce(500L)
                .distinctUntilChanged()
                .map { state().searchRequest(drinkRepository, it.query) }
                .map { state().copy(drinks = it, appliedFilters = state().selectedFilters) },
            filterIsInstance<SearchAction.FilterClicked>()
                .map {
                    state().copy(
                        selectedFilters = if (it.value in state().selectedFilters) {
                            state().selectedFilters - it.value
                        } else {
                            state().selectedFilters + it.value
                        }
                    )
                },
            filterIsInstance<SearchAction.ShowFiltersClicked>()
                .onEach { sideEffectConsumer(SearchSideEffect.ShowFilters) }
                .map { state() },
            filterIsInstance<SearchAction.FiltersDismissed>()
                .filter { state().let { it.appliedFilters != it.selectedFilters } }
                .map { state().searchRequest(drinkRepository) }
                .map { state().copy(drinks = it, appliedFilters = state().selectedFilters) },
            filterIsInstance<SearchAction.ClearSearchQuery>()
                .map { state().copy(searchQuery = "") },
            filterIsInstance<SearchAction.ClearSearchQuery>()
                .map { state().searchRequest(drinkRepository, query = "") }
                .map { state().copy(drinks = it, searchQuery = "", appliedFilters = state().selectedFilters) },
        )
    }
)

private fun SearchState.searchRequest(drinkRepository: DrinkRepository, query: String? = null) =
    drinkRepository.drinks(
        DrinksRequest.Search(
            query = query ?: searchQuery,
            filters = getFilters(aggregation(), selectedFilters)
        )
    )

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
