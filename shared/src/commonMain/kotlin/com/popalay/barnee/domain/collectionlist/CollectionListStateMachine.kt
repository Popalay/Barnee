package com.popalay.barnee.domain.collectionlist

import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.EmptySideEffect
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class CollectionListState(
    val collections: Result<Set<Collection>> = Uninitialized()
) : State

sealed interface CollectionListAction : Action {
    object Initial : CollectionListAction
}

sealed interface CollectionListMutation : Mutation {
    data class Collections(val data: Result<Set<Collection>>) : CollectionListMutation
}

class CollectionListStateMachine(
    collectionRepository: CollectionRepository,
) : StateMachine<CollectionListState, CollectionListAction, CollectionListMutation, EmptySideEffect>(
    initialState = CollectionListState(),
    initialAction = CollectionListAction.Initial,
    processor = { _, _ ->
        merge(
            filterIsInstance<CollectionListAction.Initial>()
                .take(1)
                .flatMapToResult { collectionRepository.collections() }
                .map { CollectionListMutation.Collections(it) }
        )
    },
    reducer = { mutation ->
        when (mutation) {
            is CollectionListMutation.Collections -> copy(collections = mutation.data)
        }
    }
)
