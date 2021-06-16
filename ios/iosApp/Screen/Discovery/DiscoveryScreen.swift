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
                switch viewModel.state.categories {
                case is Loading<NSArray>:
                    ProgressView("Loading…")
                case is Success<NSArray>:
                    List(viewModel.state.categories.invoke() as! [shared.Category], id: \.text) { category in
                        CategoryView(category: category)
                    }.navigationBarTitle(Text("Barnee"))
                default:
                    Text("Empty state")
                }
            }
        }
    }
}

struct CategoryView: View {
    var category: shared.Category

    var body: some View {
        HStack {
            ImageView(withURL: category.imageUrl.url, width: 64, height: 64)
            VStack(alignment: .leading) {
                Text(category.text).font(.headline)
            }
        }
    }
}
