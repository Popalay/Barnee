package com.popalay.barnee.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import kotlinx.coroutines.flow.StateFlow

abstract class StateMachineWrapperViewModel<S : State, A : Action, T : StateMachine<S, A>>(
    private val stateMachine: T
) : ViewModel() {
    val stateFlow: StateFlow<S>
        get() = stateMachine.stateFlow

    fun consumeAction(action: A) {
        stateMachine.consume(action)
    }

    override fun onCleared() {
        super.onCleared()
        stateMachine.onCleared()
    }
}