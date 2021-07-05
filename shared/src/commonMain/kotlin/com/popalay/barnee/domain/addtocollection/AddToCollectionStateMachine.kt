package com.popalay.barnee.domain.addtocollection

import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
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

sealed interface AddToCollectionMutation : Mutation {
    object Empty : AddToCollectionMutation
    data class DialogState(val data: AddToCollectionDialogState) : AddToCollectionMutation
    data class NewCollectionName(val data: String) : AddToCollectionMutation
}

sealed interface AddToCollectionSideEffect : SideEffect {
    object ShowAddToCollectionDialog : AddToCollectionSideEffect
    object HideAddToCollectionDialog : AddToCollectionSideEffect
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
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.ShowAddToCollectionDialog) }
                .map { AddToCollectionMutation.DialogState(ChooseCollectionFor(it.drink)) },
            filterIsInstance<AddToCollectionAction.CreateCollectionClicked>()
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.ShowAddToCollectionDialog) }
                .map { AddToCollectionMutation.DialogState(CreateCollectionFor(it.drink)) },
            filterIsInstance<AddToCollectionAction.BackFromCollectionCreationClicked>()
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.ShowAddToCollectionDialog) }
                .map { AddToCollectionMutation.DialogState(ChooseCollectionFor(it.drink)) },
            filterIsInstance<AddToCollectionAction.AddToCollectionDialogDismissed>()
                .map { AddToCollectionMutation.DialogState(Empty) },
            filterIsInstance<AddToCollectionAction.SaveCollectionClicked>()
                .map { collectionRepository.addToCollectionAndNotify(state().newCollectionName, it.drink) }
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.HideAddToCollectionDialog) }
                .map { AddToCollectionMutation.DialogState(Empty) },
            filterIsInstance<AddToCollectionAction.CollectionClicked>()
                .map { collectionRepository.addToCollectionAndNotify(it.collection.name, it.drink) }
                .onEach { sideEffectConsumer(AddToCollectionSideEffect.HideAddToCollectionDialog) }
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
