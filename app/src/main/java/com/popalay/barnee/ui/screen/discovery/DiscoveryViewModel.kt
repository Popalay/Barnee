package com.popalay.barnee.ui.screen.discovery

import com.popalay.barnee.domain.discovery.DiscoveryAction
import com.popalay.barnee.domain.discovery.DiscoveryState
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.ui.screen.StateMachineWrapperViewModel

class DiscoveryViewModel(
    stateMachine: DiscoveryStateMachine
) : StateMachineWrapperViewModel<DiscoveryState, DiscoveryAction>(stateMachine)