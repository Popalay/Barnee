package com.popalay.barnee.ui.screen.addtocollection

import com.popalay.barnee.domain.addtocollection.AddToCollectionAction
import com.popalay.barnee.domain.addtocollection.AddToCollectionSideEffect
import com.popalay.barnee.domain.addtocollection.AddToCollectionState
import com.popalay.barnee.domain.addtocollection.AddToCollectionStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class AddToCollectionViewModel(
    stateMachine: AddToCollectionStateMachine
) : StateMachineWrapperViewModel<AddToCollectionState, AddToCollectionAction, AddToCollectionSideEffect>(stateMachine)
