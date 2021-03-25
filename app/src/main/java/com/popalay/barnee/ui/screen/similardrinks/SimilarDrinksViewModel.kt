package com.popalay.barnee.ui.screen.similardrinks

import com.popalay.barnee.domain.similardrinks.SimilarDrinksAction
import com.popalay.barnee.domain.similardrinks.SimilarDrinksState
import com.popalay.barnee.domain.similardrinks.SimilarDrinksStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class SimilarDrinksViewModel(
    alias: String,
    stateMachine: SimilarDrinksStateMachine
) : StateMachineWrapperViewModel<SimilarDrinksState, SimilarDrinksAction, SimilarDrinksStateMachine>(stateMachine) {
    init {
        consumeAction(SimilarDrinksAction.Initial(alias))
    }
}