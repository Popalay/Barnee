import SwiftUI
import Combine
import shared

struct ContentView: View {
        
    var body: some View {
        TabView {
            DiscoveryScreen(viewModel: DiscoveryViewModel())
        }
    }
}