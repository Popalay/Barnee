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
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.SideEffect
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState.ChooseCollectionFor
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState.CreateCollectionFor
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState.Empty
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
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

sealed interface AddToCollectionSideEffect : SideEffect {
    data class DrinkAddedToFavorites(val drink: Drink) : AddToCollectionSideEffect
}

class AddToCollectionStateMachine(
    collectionRepository: CollectionRepository
) : StateMachine<AddToCollectionState, AddToCollectionAction, AddToCollectionSideEffect>(
    initialState = AddToCollectionState(),
    initialAction = AddToCollectionAction.Initial,
    reducer = { state, sideEffectConsumer ->
        merge(
            filterIsInstance<AddToCollectionAction.Initial>()
                .take(1)
                .flatMapLatest { collectionRepository.collectionsUpdate() }
                .filter { it.second != null && state().dialogState == Empty }
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.DrinkAddedToFavorites(it.first)) }
                .map { state() },
            merge(
                filterIsInstance<AddToCollectionAction.ChangeCollectionClicked>()
                    .map { ChooseCollectionFor(it.drink) },
                filterIsInstance<AddToCollectionAction.CreateCollectionClicked>()
                    .map { CreateCollectionFor(it.drink) },
                filterIsInstance<AddToCollectionAction.BackFromCollectionCreationClicked>()
                    .map { ChooseCollectionFor(it.drink) },
                filterIsInstance<AddToCollectionAction.AddToCollectionDialogDismissed>()
                    .map { Empty },
                filterIsInstance<AddToCollectionAction.SaveCollectionClicked>()
                    .map { collectionRepository.addToCollectionAndNotify(state().newCollectionName, it.drink) }
                    .map { Empty },
                filterIsInstance<AddToCollectionAction.CollectionClicked>()
                    .map { collectionRepository.addToCollectionAndNotify(it.collection.name, it.drink) }
                    .map { Empty },
            )
                .map {
                    state().copy(
                        dialogState = it,
                        newCollectionName = "",
                        isNewCollectionValid = false
                    )
                },
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
