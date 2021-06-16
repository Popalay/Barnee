package com.popalay.barnee.ui.screen.drinklist

import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.drinkitem.DrinkItemAction
import com.popalay.barnee.domain.drinkitem.DrinkItemState
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class DrinkItemViewModel(
    stateMachine: DrinkItemStateMachine
) : StateMachineWrapperViewModel<DrinkItemState, DrinkItemAction, EmptySideEffect>(stateMachine)