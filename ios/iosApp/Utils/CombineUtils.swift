//
// Created by Денис Никифоров on 15.06.2021.
// Copyright (c) 2021 orgName. All rights reserved.
//

import Foundation
import Combine
import shared

func asPublisher<T>(_ flow: CFlow<T>) -> AnyPublisher<T, Never> {
    Deferred<Publishers.HandleEvents<PassthroughSubject<T, Never>>> {
        let subject = PassthroughSubject<T, Never>()
        let closable = flow.watch { next in
            if let next = next {
                subject.send(next)
            }
        }
        return subject.handleEvents(receiveCancel: {
            closable.close()
        })
    }.eraseToAnyPublisher()
}