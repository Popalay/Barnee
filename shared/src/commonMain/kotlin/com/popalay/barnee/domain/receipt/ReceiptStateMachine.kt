package com.popalay.barnee.domain.receipt

import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.receipt.ReceiptAction.TogglePlaying

data class ReceiptState(
    val isPlaying: Boolean = false
) : State

sealed class ReceiptAction : Action {
    object TogglePlaying : ReceiptAction()
}

class ReceiptStateMachine : StateMachine<ReceiptState, ReceiptAction>(ReceiptState()) {
    override fun reducer(currentState: ReceiptState, action: ReceiptAction) {
        when (action) {
            is TogglePlaying -> setState { copy(isPlaying = !isPlaying) }
        }
    }
}