package com.popalay.barnee.ui.screen.collectionlist

import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.collectionlist.CollectionListAction
import com.popalay.barnee.domain.collectionlist.CollectionListState
import com.popalay.barnee.domain.collectionlist.CollectionListStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class CollectionListViewModel(
    stateMachine: CollectionListStateMachine
) : StateMachineWrapperViewModel<CollectionListState, CollectionListAction, EmptySideEffect>(stateMachine)
