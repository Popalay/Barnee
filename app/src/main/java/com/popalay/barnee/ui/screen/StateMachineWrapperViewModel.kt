package com.popalay.barnee.ui.screen

import androidx.lifecycle.ViewModel
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.StateFlow

abstract class StateMachineWrapperViewModel<S : State, A : Action>(
    private val stateMachine: StateMachine<S, A, *>
) : ViewModel() {
    val stateFlow: StateFlow<S>
        get() = stateMachine.stateFlow

    fun processAction(action: A) {
        stateMachine.process(action)
    }

    override fun onCleared() {
        super.onCleared()
        stateMachine.onCleared()
    }
}