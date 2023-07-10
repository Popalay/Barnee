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

package com.popalay.barnee.domain.addtocollection

import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState.ChooseCollection
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState.CreateCollection
import com.popalay.barnee.domain.navigation.NavigateBackAction
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

data class AddToCollectionState(
    val drink: DrinkMinimumData,
    val newCollectionName: String = "",
    val isNewCollectionValid: Boolean = false,
    val dialogState: AddToCollectionDialogState = ChooseCollection
) : State

sealed interface AddToCollectionDialogState {
    object ChooseCollection : AddToCollectionDialogState
    object CreateCollection : AddToCollectionDialogState
}

sealed interface AddToCollectionAction : Action {
    object SaveCollectionClicked : AddToCollectionAction
    object CreateCollectionClicked : AddToCollectionAction
    object BackFromCollectionCreationClicked : AddToCollectionAction
    data class NewCollectionNameChanged(val name: String) : AddToCollectionAction
    data class CollectionClicked(val collection: Collection) : AddToCollectionAction
}

class AddToCollectionStateMachine(
    drink: DrinkMinimumData,
    collectionRepository: CollectionRepository,
) : StateMachine<AddToCollectionState>(
    initialState = AddToCollectionState(drink),
    reducer = { state, dispatcher ->
        merge(
            merge(
                filterIsInstance<AddToCollectionAction.CreateCollectionClicked>()
                    .map { CreateCollection },
                filterIsInstance<AddToCollectionAction.BackFromCollectionCreationClicked>()
                    .map { ChooseCollection },
            )
                .map {
                    state().copy(
                        dialogState = it,
                        newCollectionName = "",
                        isNewCollectionValid = false
                    )
                },
            filterIsInstance<AddToCollectionAction.SaveCollectionClicked>()
                .map { collectionRepository.addToCollection(state().newCollectionName, state().drink) }
                .onEach { dispatcher(NavigateBackAction) }
                .map { state() },
            filterIsInstance<AddToCollectionAction.CollectionClicked>()
                .map { collectionRepository.addToCollection(it.collection.name, state().drink) }
                .onEach { dispatcher(NavigateBackAction) }
                .map { state() },
            filterIsInstance<AddToCollectionAction.NewCollectionNameChanged>()
                .map {
                    state().copy(
                        newCollectionName = it.name,
                        isNewCollectionValid = it.name.isNotBlank()
                    )
                },
        )
    }
)
