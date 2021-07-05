package com.popalay.barnee.ui.screen.collection

import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.collection.CollectionAction
import com.popalay.barnee.domain.collection.CollectionState
import com.popalay.barnee.domain.collection.CollectionStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class CollectionViewModel(
    stateMachine: CollectionStateMachine
) : StateMachineWrapperViewModel<CollectionState, CollectionAction, EmptySideEffect>(stateMachine)
