//
// Created by Denys Nykyforov on 26/3/21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

class StateMachineWrapperViewModel<S: shared.State, SE: SideEffect, T: StateMachine<S, A, SE>>: ObservableObject {
    private let stateMachine: T
    @Published var state: S

    init(stateMachine: T, initialState: S) {
        self.stateMachine = stateMachine
        state = initialState
        asPublisher(stateMachine.stateFlow)
                .compactMap {
                    $0
                }
                .receive(on: DispatchQueue.main)
                .assign(to: &$state)
    }

    func dispatchAction(action: A) {
        stateMachine.dispatch(action: action)
    }
}
