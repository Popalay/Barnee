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

package com.popalay.barnee.domain.state.collection

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.data.repository.ShareRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.state.navigation.BackDestination
import com.popalay.barnee.domain.state.navigation.Router
import com.popalay.barnee.domain.usecase.GetCollectionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

data class CollectionInput(
    val name: String,
    val aliases: Set<String>
) : Input

data class CollectionState(
    val name: String,
    val aliases: Set<String>,
    val collection: Collection? = null,
    val isRemoveButtonVisible: Boolean = false,
    val isSaveButtonVisible: Boolean = false,
    val isShareButtonVisible: Boolean = false,
    val drinks: Flow<PagingData<Drink>> = emptyFlow()
) : State {
    constructor(input: CollectionInput) : this(input.name, input.aliases)
}

sealed interface CollectionAction : Action {
    object Initial : CollectionAction
    object RemoveClicked : CollectionAction
    object ShareClicked : CollectionAction
    object SaveClicked : CollectionAction
}

class CollectionStateMachine(
    input: CollectionInput,
    collectionRepository: CollectionRepository,
    shareRepository: ShareRepository,
    getCollectionUseCase: GetCollectionUseCase,
    router: Router
) : StateMachine<CollectionState, CollectionAction, EmptySideEffect>(
    initialState = CollectionState(input),
    initialAction = CollectionAction.Initial,
    reducer = { state, _ ->
        merge(
            filterIsInstance<CollectionAction.Initial>()
                .take(1)
                .flatMapLatest { getCollectionUseCase(GetCollectionUseCase.Input(state().name, state().aliases)) }
                .map {
                    state().copy(
                        drinks = it.pagingDataFlow,
                        collection = it.collection,
                        isRemoveButtonVisible = it.isCollectionExists && state().name != Collection.DEFAULT_NAME,
                        isShareButtonVisible = it.isCollectionExists,
                        isSaveButtonVisible = !it.isCollectionExists
                    )
                },
            filterIsInstance<CollectionAction.RemoveClicked>()
                .map { collectionRepository.remove(state().name) }
                .onEach { router.navigate(BackDestination) }
                .map { state() },
            filterIsInstance<CollectionAction.ShareClicked>()
                .map { state().collection?.let { shareRepository.shareCollection(it) } }
                .map { state() },
            filterIsInstance<CollectionAction.SaveClicked>()
                .map { collectionRepository.saveOrMerge(state().name, state().aliases) }
                .map { state() }
        )
    }
)
