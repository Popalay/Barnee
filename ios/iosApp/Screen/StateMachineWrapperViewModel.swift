//
// Created by Denys Nykyforov on 26/3/21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

class StateMachineWrapperViewModel<S: shared.State, A: Action, T: StateMachine<S, A>>: ObservableObject {
    private let stateMachine: T
    @Published var state: S

    init(stateMachine: T, initialState: S) {
        self.stateMachine = stateMachine
        self.state = initialState
        stateMachine.onChange { newState in
            self.state = newState
        }
    }

    func consumeAction(action: A) {
        stateMachine.consume(action: action)
    }
}