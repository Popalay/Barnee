import Foundation
import Combine
import SwiftUI
import shared

class ViewModel: ObservableObject {
    @Published var drinks = [Drink]()
    
    private var disposables = Set<AnyCancellable>()
    
    init() {
        let sharedComponent = (UIApplication.shared.delegate as! AppDelegate).sharedComponent
        createPublisher(wrapper: sharedComponent.provideDrinkRepository().getRandomDrinks(count: 10))
            .sink(
                receiveCompletion: { _ in },
                receiveValue: { [weak self] drinks in
                    self?.drinks = drinks as! Array<Drink>
                }
            )
            .store(in: &disposables)
    }

}
