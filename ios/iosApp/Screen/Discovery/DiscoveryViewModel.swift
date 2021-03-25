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

class DiscoveryViewModel : ObservableObject {
    let stateMachine : DiscoveryStateMachine
    @Published var state : DiscoveryState = DiscoveryState(drinks: Uninitialized())
    
    init() {
        let sharedComponent = (UIApplication.shared.delegate as! AppDelegate).sharedComponent
        stateMachine = sharedComponent.provideDiscoveryStateMachine()
        stateMachine.onChange { newState in
            self.state = newState
        }
        stateMachine.consume(action: DiscoveryAction.Initial())
    }
}
