package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.drink.DrinkAction.Initial
import com.popalay.barnee.domain.drink.DrinkAction.ToggleFavorite
import com.popalay.barnee.domain.drink.DrinkAction.TogglePlaying
import com.popalay.barnee.domain.drink.DrinkMutation.DrinkWithRelatedMutation
import com.popalay.barnee.domain.drink.DrinkMutation.ToggleFavoriteMutation
import com.popalay.barnee.domain.drink.DrinkMutation.TogglePlayingMutation
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class DrinkState(
    val drinkWithRelated: Result<FullDrinkResponse> = Uninitialized(),
    val isPlaying: Boolean = false
) : State

sealed class DrinkAction : Action {
    data class Initial(val alias: String) : DrinkAction()
    data class ToggleFavorite(val alias: String) : DrinkAction()
    object TogglePlaying : DrinkAction()
}

sealed class DrinkMutation : Mutation {
    data class DrinkWithRelatedMutation(val data: Result<FullDrinkResponse>) : DrinkMutation()
    data class ToggleFavoriteMutation(val data: Boolean) : DrinkMutation()
    data class TogglePlayingMutation(val data: Boolean) : DrinkMutation()
}

class DrinkStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction, DrinkMutation>(DrinkState()) {
    override val processor: Processor<DrinkState, DrinkMutation> = { state ->
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFullDrink(it.alias) }
                .map { DrinkWithRelatedMutation(it) },
            filterIsInstance<ToggleFavorite>()
                .map { drinkRepository.toggleFavoriteFor(it.alias) }
                .map { ToggleFavoriteMutation(it) },
            filterIsInstance<TogglePlaying>()
                .map { !state().isPlaying }
                .map { TogglePlayingMutation(it) },
        )
    }

    override val reducer: Reducer<DrinkState, DrinkMutation> = { mutation ->
        when (mutation) {
            is DrinkWithRelatedMutation -> copy(drinkWithRelated = mutation.data)
            is ToggleFavoriteMutation -> this
            is TogglePlayingMutation -> copy(isPlaying = mutation.data)
        }
    }
}