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

package com.popalay.barnee.domain.state.addtocollection

import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.core.Action
import com.popalay.barnee.domain.core.Mutation
import com.popalay.barnee.domain.core.SideEffect
import com.popalay.barnee.domain.core.State
import com.popalay.barnee.domain.core.StateMachine
import com.popalay.barnee.domain.state.addtocollection.AddToCollectionDialogState.Empty
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class AddToCollectionState(
    val newCollectionName: String = "",
    val isNewCollectionValid: Boolean = false,
    val dialogState: AddToCollectionDialogState = Empty
) : State

sealed class AddToCollectionDialogState {
    object Empty : AddToCollectionDialogState()
    data class ChooseCollectionFor(val drink: Drink) : AddToCollectionDialogState()
    data class CreateCollectionFor(val drink: Drink) : AddToCollectionDialogState()
}

sealed interface AddToCollectionAction : Action {
    object Initial : AddToCollectionAction
    object AddToCollectionDialogDismissed : AddToCollectionAction
    data class SaveCollectionClicked(val drink: Drink) : AddToCollectionAction
    data class CreateCollectionClicked(val drink: Drink) : AddToCollectionAction
    data class BackFromCollectionCreationClicked(val drink: Drink) : AddToCollectionAction
    data class ChangeCollectionClicked(val drink: Drink) : AddToCollectionAction
    data class NewCollectionNameChanged(val name: String) : AddToCollectionAction
    data class CollectionClicked(val collection: Collection, val drink: Drink) : AddToCollectionAction
}

sealed interface AddToCollectionMutation : Mutation {
    object Empty : AddToCollectionMutation
    data class DialogState(val data: AddToCollectionDialogState) : AddToCollectionMutation
    data class NewCollectionName(val data: String) : AddToCollectionMutation
}

sealed interface AddToCollectionSideEffect : SideEffect {
    data class DrinkAddedToFavorites(val drink: Drink) : AddToCollectionSideEffect
}

class AddToCollectionStateMachine(
    collectionRepository: CollectionRepository
) : StateMachine<AddToCollectionState, AddToCollectionAction, AddToCollectionMutation, AddToCollectionSideEffect>(
    initialState = AddToCollectionState(),
    initialAction = AddToCollectionAction.Initial,
    processor = { state, sideEffectConsumer ->
        merge(
            filterIsInstance<AddToCollectionAction.Initial>()
                .take(1)
                .flatMapLatest { collectionRepository.collectionsUpdate() }
                .filter { it.collection != null && state().dialogState == Empty }
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.DrinkAddedToFavorites(it)) }
                .map { AddToCollectionMutation.Empty },
            filterIsInstance<AddToCollectionAction.ChangeCollectionClicked>()
                .map { AddToCollectionMutation.DialogState(AddToCollectionDialogState.ChooseCollectionFor(it.drink)) },
            filterIsInstance<AddToCollectionAction.CreateCollectionClicked>()
                .map { AddToCollectionMutation.DialogState(AddToCollectionDialogState.CreateCollectionFor(it.drink)) },
            filterIsInstance<AddToCollectionAction.BackFromCollectionCreationClicked>()
                .map { AddToCollectionMutation.DialogState(AddToCollectionDialogState.ChooseCollectionFor(it.drink)) },
            filterIsInstance<AddToCollectionAction.AddToCollectionDialogDismissed>()
                .map { AddToCollectionMutation.DialogState(Empty) },
            filterIsInstance<AddToCollectionAction.SaveCollectionClicked>()
                .map { collectionRepository.addToCollectionAndNotify(state().newCollectionName, it.drink) }
                .map { AddToCollectionMutation.DialogState(Empty) },
            filterIsInstance<AddToCollectionAction.CollectionClicked>()
                .map { collectionRepository.addToCollectionAndNotify(it.collection.name, it.drink) }
                .map { AddToCollectionMutation.DialogState(Empty) },
            filterIsInstance<AddToCollectionAction.NewCollectionNameChanged>()
                .map { AddToCollectionMutation.NewCollectionName(it.name) },
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is AddToCollectionMutation.DialogState -> copy(
                dialogState = mutation.data,
                newCollectionName = "",
                isNewCollectionValid = false
            )
            is AddToCollectionMutation.NewCollectionName -> copy(
                newCollectionName = mutation.data,
                isNewCollectionValid = mutation.data.isNotBlank()
            )
            is AddToCollectionMutation.Empty -> this
        }
    }
)
