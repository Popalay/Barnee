import Foundation
import shared
import Combine

class SharedError: LocalizedError {
    let throwable: KotlinThrowable
    init(_ throwable: KotlinThrowable) {
        self.throwable = throwable
    }
    var errorDescription: String? {
        get { throwable.message }
    }
}

/* Unfortunately we lose the nullability info on the generic type T,
 so you may need to force unwrap. More info:
 https://kotlinlang.org/docs/reference/native/objc_interop.html#nullability
 */
func createPublisher<T>(
    wrapper: KoruSuspendWrapper<T>
) -> AnyPublisher<T?, Error> {
    var job: Kotlinx_coroutines_coreJob? = nil
    return Deferred {
        Future<T?, Error> { promise in
            job = wrapper.subscribe(
                onSuccess: { item in promise(.success(item)) },
                onThrow: { error in promise(.failure(SharedError(error))) }
            )
        }.handleEvents(receiveCancel: { job?.cancel(cause: nil) })
    }.eraseToAnyPublisher()
}

func createPublisher<T>(
    scope: Kotlinx_coroutines_coreCoroutineScope,
    wrapper: KoruSuspendWrapper<T>
) -> AnyPublisher<T?, Error> {
    var job: Kotlinx_coroutines_coreJob? = nil
    return Deferred {
        Future<T?, Error> { promise in
            job = wrapper.subscribe(
                scope: scope,
                onSuccess: { item in promise(.success(item)) },
                onThrow: { error in promise(.failure(SharedError(error))) }
            )
        }.handleEvents(receiveCancel: { job?.cancel(cause: nil) })
    }.eraseToAnyPublisher()
}

//func createPublisher<T>(
//    wrapper: KoruFlowWrapper<T>
//) -> AnyPublisher<T?, Error> {
//    let subject = PassthroughSubject<T?, Error>()
//    var job: Kotlinx_coroutines_coreJob? = nil
//    return subject
//        .handleEvents(receiveSubscription: { subscription in
//            job = wrapper.subscribe(
//                onEach: { item in subject.send(item) },
//                onComplete: { subject.send(completion: .finished) },
//                onThrow: { error in subject.send(completion: .failure(SharedError(error))) }
//            )
//        }, receiveCancel: { job?.cancel(cause: nil) })
//        .eraseToAnyPublisher()
//}
//
//func createPublisher<T>(
//    scope: Kotlinx_coroutines_coreCoroutineScope,
//    wrapper: KoruFlowWrapper<T>
//) -> AnyPublisher<T?, Error> {
//    let subject = PassthroughSubject<T?, Error>()
//    var job: Kotlinx_coroutines_coreJob? = nil
//    return subject
//        .handleEvents(receiveSubscription: { subscription in
//            job = wrapper.subscribe(
//                scope: scope,
//                onEach: { item in subject.send(item) },
//                onComplete: { subject.send(completion: .finished) },
//                onThrow: { error in subject.send(completion: .failure(SharedError(error))) }
//            )
//        }, receiveCancel: { job?.cancel(cause: nil) })
//        .eraseToAnyPublisher()
//}
