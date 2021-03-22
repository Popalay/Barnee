package com.popalay.barnee.ui.screen.receipt

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel

data class ReceiptState(
    val isPlaying: Boolean = false
) : MavericksState

class ReceiptViewModel(initialState: ReceiptState) : MavericksViewModel<ReceiptState>(initialState) {
    fun togglePlaying() {
        setState { copy(isPlaying = !isPlaying) }
    }
}