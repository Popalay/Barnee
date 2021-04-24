package com.popalay.barnee.ui.screen.shaketodrink

import com.popalay.barnee.domain.shakedrink.ShakeToDrinkAction
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkState
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class ShakeToDrinkViewModel(
    stateMachine: ShakeToDrinkStateMachine
) : StateMachineWrapperViewModel<ShakeToDrinkState, ShakeToDrinkAction>(stateMachine)