package com.popalay.barnee.ui.screen.parameterizeddrinklist

import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListAction
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListState
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class ParameterizedDrinkListViewModel(
    request: DrinksRequest,
    stateMachine: ParameterizedDrinkListStateMachine
) : StateMachineWrapperViewModel<ParameterizedDrinkListState, ParameterizedDrinkListAction>(stateMachine) {
    init {
        processAction(ParameterizedDrinkListAction.Initial(request))
    }
}