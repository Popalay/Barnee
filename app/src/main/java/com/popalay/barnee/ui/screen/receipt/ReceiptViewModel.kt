package com.popalay.barnee.ui.screen.receipt

import com.popalay.barnee.domain.receipt.ReceiptAction
import com.popalay.barnee.domain.receipt.ReceiptState
import com.popalay.barnee.domain.receipt.ReceiptStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class ReceiptViewModel(
    stateMachine: ReceiptStateMachine
) : StateMachineWrapperViewModel<ReceiptState, ReceiptAction, ReceiptStateMachine>(stateMachine)