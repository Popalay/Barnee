package com.popalay.barnee.ui.screen.drink

import com.popalay.barnee.domain.drink.DrinkAction
import com.popalay.barnee.domain.drink.DrinkState
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class DrinkViewModel(
    alias: String,
    stateMachine: DrinkStateMachine
) : StateMachineWrapperViewModel<DrinkState, DrinkAction, DrinkStateMachine>(stateMachine) {
    init {
        consumeAction(DrinkAction.Initial(alias))
    }
}