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
import resu

struct DiscoveryScreen: View {
    @ObservedObject var viewModel: DiscoveryViewModel
    
    var body: some View {
        NavigationView {
            VStack {
                List(viewModel.state.drinks.get()!!, id: \.name) { drink in
                    DrinkView(drink: drink)
                }
                .navigationBarTitle(Text("Barnee"))
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
