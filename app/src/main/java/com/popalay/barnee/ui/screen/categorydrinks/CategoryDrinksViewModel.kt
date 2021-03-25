package com.popalay.barnee.ui.screen.categorydrinks

import com.popalay.barnee.domain.categorydrinks.CategoryDrinksAction
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksState
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class CategoryDrinksViewModel(
    tag: String,
    stateMachine: CategoryDrinksStateMachine
) : StateMachineWrapperViewModel<CategoryDrinksState, CategoryDrinksAction, CategoryDrinksStateMachine>(stateMachine) {
    init {
        consumeAction(CategoryDrinksAction.Initial(tag))
    }
}