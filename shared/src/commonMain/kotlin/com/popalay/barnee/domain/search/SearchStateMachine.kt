package com.popalay.barnee.domain.search

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.search.SearchAction.ApplyClicked
import com.popalay.barnee.domain.search.SearchAction.FilterClicked
import com.popalay.barnee.domain.search.SearchAction.Initial
import com.popalay.barnee.domain.search.SearchAction.QueryChanged
import com.popalay.barnee.util.ConflatedJob
import kotlinx.coroutines.delay

data class SearchState(
    val searchQuery: String = "",
    val drinks: Result<List<Drink>> = Uninitialized(),
    val aggregation: Result<Aggregation> = Uninitialized(),
    val selectedGroups: Set<Pair<String, AggregationGroup>> = emptySet(),
    val isBackDropRevealed: Boolean = false
) : State

sealed class SearchAction : Action {
    object Initial : SearchAction()
    object ApplyClicked : SearchAction()
    data class QueryChanged(val query: String) : SearchAction()
    data class FilterClicked(val value: Pair<String, AggregationGroup>) : SearchAction()
}

class SearchStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<SearchState, SearchAction>(SearchState()) {
    private val searchJob = ConflatedJob()

    override fun reducer(currentState: SearchState, action: SearchAction) {
        when (action) {
            Initial -> suspend { drinkRepository.getAggregation() }.execute { copy(aggregation = it) }
            ApplyClicked -> {
                setState { copy(isBackDropRevealed = true) }
                suspend { requestSearch() }.execute { copy(drinks = it) }
            }
            is QueryChanged -> {
                setState { copy(searchQuery = action.query) }
                searchJob += suspend {
                    delay(500)
                    requestSearch()
                }.execute { copy(drinks = it) }
            }
            is FilterClicked -> {
                setState {
                    if (action.value in selectedGroups) {
                        copy(selectedGroups = selectedGroups - action.value)
                    } else {
                        copy(selectedGroups = selectedGroups + action.value)
                    }
                }
            }
        }
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
}