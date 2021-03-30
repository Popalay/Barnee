package com.popalay.barnee.domain.categorydrinks

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Output
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksAction.Initial
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksOutput.DrinksOutput
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class CategoryDrinksState(
    val drinks: Result<List<Drink>> = Uninitialized(),
) : State

sealed class CategoryDrinksAction : Action {
    data class Initial(val tag: String) : CategoryDrinksAction()
}

sealed class CategoryDrinksOutput : Output {
    data class DrinksOutput(val data: Result<List<Drink>>) : CategoryDrinksOutput()
}

class CategoryDrinksStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<CategoryDrinksState, CategoryDrinksAction, CategoryDrinksOutput>(CategoryDrinksState()) {
    override val processor: Processor<CategoryDrinksState, CategoryDrinksOutput> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .mapToResult { drinkRepository.getDrinksByTags(listOf(it.tag)) }
                .map { DrinksOutput(it) }
        )
    }

    override val reducer: Reducer<CategoryDrinksState, CategoryDrinksOutput> = { result ->
        when (result) {
            is DrinksOutput -> copy(drinks = result.data)
        }
    }
}