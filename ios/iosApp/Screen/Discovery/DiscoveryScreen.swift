//
//  DiscoveryScreen.swift
//  iosApp
//
//  Created by Denys Nykyforov on 25/3/21.
//  Copyright © 2021 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct DiscoveryScreen: View {
    @ObservedObject var viewModel: DiscoveryViewModel
    
    var body: some View {
        NavigationView {
            VStack {
                switch viewModel.state.drinks {
                case is Loading<NSArray>:
                    ProgressView("Loading…")
                case is Success<NSArray>:
                    List(viewModel.state.drinks.invoke() as! [Drink], id: \.name) { drink in
                        DrinkView(drink: drink)
                    }
                    .navigationBarTitle(Text("Barnee"))
                default :
                    Text("Empty state")
                }
            }
        }
    }
}

struct DrinkView: View {
    var drink: Drink
    
    var body: some View {
        HStack {
            ImageView(withURL:drink.displayImageUrl, width: 64, height: 64)
            VStack(alignment: .leading) {
                Text(drink.name).font(.headline)
                Text(String(drink.rating)).font(.subheadline)
            }
        }
    }
}
