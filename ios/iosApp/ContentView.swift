import SwiftUI
import Combine
import shared

struct ContentView: View {
    
    @ObservedObject var viewModel: ViewModel
    
    var body: some View {
        TabView {
            DrinksListView(viewModel: viewModel)
                .tabItem {
                    Label("People", systemImage: "person")
                }
        }
    }
}

struct DrinksListView: View {
    @ObservedObject var viewModel: ViewModel
    
    var body: some View {
        NavigationView {
            VStack {
                List(viewModel.drinks, id: \.name) { drink in
                    DrinkView(viewModel: viewModel, drink: drink)
                }
                .navigationBarTitle(Text("Barnee"))
            }
        }
    }
}

struct DrinkView: View {
    var viewModel: ViewModel
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

