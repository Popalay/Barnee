/*
 * Copyright (c) 2023 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.domain.collectionlist

import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.NoSideEffect
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import com.popalay.barnee.domain.navigation.CollectionDestination
import com.popalay.barnee.domain.navigation.Router
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

data class CollectionListState(
    val collections: Result<Set<Collection>> = Uninitialized()
) : State

sealed interface CollectionListAction : Action {
    object Initial : CollectionListAction
    data class CollectionClicked(val collection: Collection) : CollectionListAction
}

class CollectionListStateMachine(
    collectionRepository: CollectionRepository,
    router: Router
) : StateMachine<CollectionListState, CollectionListAction, NoSideEffect>(
    initialState = CollectionListState(),
    initialAction = CollectionListAction.Initial,
    reducer = { state, _ ->
        merge(
            filterIsInstance<CollectionListAction.Initial>()
                .take(1)
                .flatMapToResult { collectionRepository.collections() }
                .map { state().copy(collections = it) },
            filterIsInstance<CollectionListAction.CollectionClicked>()
                .onEach { router.navigate(CollectionDestination(it.collection)) }
                .map { state() }
        )
    }
)
