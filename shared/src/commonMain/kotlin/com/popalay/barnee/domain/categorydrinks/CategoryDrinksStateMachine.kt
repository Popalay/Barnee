package com.popalay.barnee.domain.categorydrinks

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksAction.Initial
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksMutation.DrinksMutation
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

sealed class CategoryDrinksMutation : Mutation {
    data class DrinksMutation(val data: Result<List<Drink>>) : CategoryDrinksMutation()
}

class CategoryDrinksStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<CategoryDrinksState, CategoryDrinksAction, CategoryDrinksMutation>(CategoryDrinksState()) {
    override val processor: Processor<CategoryDrinksState, CategoryDrinksMutation> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getDrinksByTags(listOf(it.tag)) }
                .map { DrinksMutation(it) }
        )
    }

    override val reducer: Reducer<CategoryDrinksState, CategoryDrinksMutation> = { mutation ->
        when (mutation) {
            is DrinksMutation -> copy(drinks = mutation.data)
        }
    }
}