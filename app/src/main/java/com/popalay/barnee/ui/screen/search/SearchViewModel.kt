package com.popalay.barnee.ui.screen.search

import com.popalay.barnee.domain.search.SearchAction
import com.popalay.barnee.domain.search.SearchSideEffect
import com.popalay.barnee.domain.search.SearchState
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class SearchViewModel(
    stateMachine: SearchStateMachine
) : StateMachineWrapperViewModel<SearchState, SearchAction, SearchSideEffect>(stateMachine)
