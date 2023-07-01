//
//  DiscoveryViewModel.swift
//  iosApp
//
//  Created by Denys Nykyforov on 25/3/21.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

class DiscoveryViewModel: StateMachineWrapperViewModel<DiscoveryState, DiscoveryMutation, EmptySideEffect, DiscoveryStateMachine> {
    convenience init() {
        let sharedComponent = (UIApplication.shared.delegate as! AppDelegate).sharedComponent
        self.init(stateMachine: sharedComponent.provideDiscoveryStateMachine(), initialState: DiscoveryState(categories: Uninitialized()))
    }
}
