package com.popalay.barnee.ui.screen.drinklist

import com.popalay.barnee.domain.drinklist.DrinkListAction
import com.popalay.barnee.domain.drinklist.DrinkListState
import com.popalay.barnee.domain.drinklist.DrinkListStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class DrinkListViewModel(
    stateMachine: DrinkListStateMachine
) : StateMachineWrapperViewModel<DrinkListState, DrinkListAction>(stateMachine)