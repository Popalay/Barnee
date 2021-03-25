package com.popalay.barnee.ui.screen.favorites

import com.popalay.barnee.domain.favorites.FavoritesAction
import com.popalay.barnee.domain.favorites.FavoritesState
import com.popalay.barnee.domain.favorites.FavoritesStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class FavoritesViewModel(
    stateMachine: FavoritesStateMachine
) : StateMachineWrapperViewModel<FavoritesState, FavoritesAction, FavoritesStateMachine>(stateMachine) {
    init {
        consumeAction(FavoritesAction.Initial)
    }
}