package com.popalay.barnee.ui.screen.drinklist

import com.popalay.barnee.domain.drinkitem.DrinkItemAction
import com.popalay.barnee.domain.drinkitem.DrinkItemState
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class DrinkListViewModel(
    stateMachine: DrinkItemStateMachine
) : StateMachineWrapperViewModel<DrinkItemState, DrinkItemAction>(stateMachine)