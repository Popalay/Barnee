package com.popalay.barnee.domain.categorydrinks

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksAction.Initial

data class CategoryDrinksState(
    val drinks: Result<List<Drink>> = Uninitialized(),
) : State

sealed class CategoryDrinksAction : Action {
    data class Initial(val tag: String) : CategoryDrinksAction()
}

class CategoryDrinksStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<CategoryDrinksState, CategoryDrinksAction>(CategoryDrinksState()) {
    override fun reducer(currentState: CategoryDrinksState, action: CategoryDrinksAction) {
        when (action) {
            is Initial -> suspend { drinkRepository.getDrinksByTags(listOf(action.tag)) }.execute { copy(drinks = it) }
        }
    }
}