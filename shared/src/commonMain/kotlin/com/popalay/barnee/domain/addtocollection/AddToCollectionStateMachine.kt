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

package com.popalay.barnee.domain.addtocollection

import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.SideEffect
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import com.popalay.barnee.domain.navigation.BackDestination
import com.popalay.barnee.domain.navigation.Router
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

data class AddToCollectionInput(
    val alias: String
) : Input

data class AddToCollectionState(
    val alias: String,
    val drink: Result<Drink> = Uninitialized(),
    val newCollectionName: String = "",
    val isNewCollectionValid: Boolean = false,
    val dialogState: AddToCollectionDialogState = AddToCollectionDialogState.ChooseCollection
) : State {
    constructor(input: AddToCollectionInput) : this(input.alias)
}

enum class AddToCollectionDialogState {
    ChooseCollection,
    CreateCollection
}

sealed interface AddToCollectionAction : Action {
    object Initial : AddToCollectionAction
    object SaveCollectionClicked : AddToCollectionAction
    object CreateCollectionClicked : AddToCollectionAction
    object BackFromCollectionCreationClicked : AddToCollectionAction
    object ChangeCollectionClicked : AddToCollectionAction
    data class NewCollectionNameChanged(val name: String) : AddToCollectionAction
    data class CollectionClicked(val collection: Collection) : AddToCollectionAction
}

object AddToCollectionSideEffect : SideEffect

class AddToCollectionStateMachine(
    input: AddToCollectionInput,
    collectionRepository: CollectionRepository,
    drinkRepository: DrinkRepository,
    router: Router
) : StateMachine<AddToCollectionState, AddToCollectionAction, AddToCollectionSideEffect>(
    initialState = AddToCollectionState(input),
    initialAction = AddToCollectionAction.Initial,
    reducer = { state, _ ->
        merge(
            filterIsInstance<AddToCollectionAction.Initial>()
                .flatMapToResult { drinkRepository.drink(state().alias) }
                .map { state().copy(drink = it) },
            merge(
                filterIsInstance<AddToCollectionAction.ChangeCollectionClicked>()
                    .map { AddToCollectionDialogState.ChooseCollection },
                filterIsInstance<AddToCollectionAction.CreateCollectionClicked>()
                    .map { AddToCollectionDialogState.CreateCollection },
                filterIsInstance<AddToCollectionAction.BackFromCollectionCreationClicked>()
                    .map { AddToCollectionDialogState.ChooseCollection },
            )
                .map {
                    state().copy(
                        dialogState = it,
                        newCollectionName = "",
                        isNewCollectionValid = false
                    )
                },
            filterIsInstance<AddToCollectionAction.SaveCollectionClicked>()
                .map { state().drink()?.let { collectionRepository.addToCollection(state().newCollectionName, it) } }
                .onEach { router.navigate(BackDestination) }
                .map { state() },
            filterIsInstance<AddToCollectionAction.CollectionClicked>()
                .map { action -> state().drink()?.let { collectionRepository.addToCollection(action.collection.name, it) } }
                .onEach { router.navigate(BackDestination) }
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
