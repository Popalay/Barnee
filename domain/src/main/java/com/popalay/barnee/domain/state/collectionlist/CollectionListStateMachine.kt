/*
 * Copyright (c) 2021 Denys Nykyforov
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

package com.popalay.barnee.domain.state.collectionlist

import com.popalay.barnee.domain.core.Action
import com.popalay.barnee.domain.core.EmptySideEffect
import com.popalay.barnee.domain.core.Mutation
import com.popalay.barnee.domain.core.Result
import com.popalay.barnee.domain.core.State
import com.popalay.barnee.domain.core.StateMachine
import com.popalay.barnee.domain.core.Uninitialized
import com.popalay.barnee.domain.state.navigation.Router
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

sealed interface CollectionListMutation : Mutation {
    object Nothing : CollectionListMutation
    data class Collections(val data: Result<Set<Collection>>) : CollectionListMutation
}

class CollectionListStateMachine(
    collectionRepository: CollectionRepository,
    router: Router
) : StateMachine<CollectionListState, CollectionListAction, CollectionListMutation, EmptySideEffect>(
    initialState = CollectionListState(),
    initialAction = CollectionListAction.Initial,
    processor = { _, _ ->
        merge(
            filterIsInstance<CollectionListAction.Initial>()
                .take(1)
                .flatMapToResult { collectionRepository.collections() }
                .map { CollectionListMutation.Collections(it) },
            filterIsInstance<CollectionListAction.CollectionClicked>()
                .onEach { router.navigate(CollectionDestination(it.collection)) }
                .map { CollectionListMutation.Nothing }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is CollectionListMutation.Collections -> copy(collections = mutation.data)
            is CollectionListMutation.Nothing -> this
        }
    }
)
