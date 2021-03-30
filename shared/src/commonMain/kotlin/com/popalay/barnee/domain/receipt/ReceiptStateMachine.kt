package com.popalay.barnee.domain.receipt

import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Output
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.receipt.ReceiptAction.TogglePlaying
import com.popalay.barnee.domain.receipt.ReceiptOutput.TogglePlayingOutput
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class ReceiptState(
    val isPlaying: Boolean = false
) : State

sealed class ReceiptAction : Action {
    object TogglePlaying : ReceiptAction()
}

sealed class ReceiptOutput : Output {
    data class TogglePlayingOutput(val data: Boolean) : ReceiptOutput()
}

class ReceiptStateMachine : StateMachine<ReceiptState, ReceiptAction, ReceiptOutput>(ReceiptState()) {
    override val processor: Processor<ReceiptState, ReceiptOutput> = { state ->
        merge(
            filterIsInstance<TogglePlaying>()
                .map { !state.isPlaying }
                .map { TogglePlayingOutput(it) },
        )
    }

    override val reducer: Reducer<ReceiptState, ReceiptOutput> = { result ->
        when (result) {
            is TogglePlayingOutput -> copy(isPlaying = result.data)
        }
    }
}