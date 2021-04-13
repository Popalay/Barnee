package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Output
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.drink.DrinkAction.Initial
import com.popalay.barnee.domain.drink.DrinkAction.ToggleFavorite
import com.popalay.barnee.domain.drink.DrinkAction.TogglePlaying
import com.popalay.barnee.domain.drink.DrinkOutput.DrinkWithRelatedOutput
import com.popalay.barnee.domain.drink.DrinkOutput.ToggleFavoriteOutput
import com.popalay.barnee.domain.drink.DrinkOutput.TogglePlayingOutput
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

sealed class DrinkOutput : Output {
    data class DrinkWithRelatedOutput(val data: Result<FullDrinkResponse>) : DrinkOutput()
    data class ToggleFavoriteOutput(val data: Boolean) : DrinkOutput()
    data class TogglePlayingOutput(val data: Boolean) : DrinkOutput()
}

class DrinkStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction, DrinkOutput>(DrinkState()) {
    override val processor: Processor<DrinkState, DrinkOutput> = { state ->
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getFullDrink(it.alias) }
                .map { DrinkWithRelatedOutput(it) },
            filterIsInstance<ToggleFavorite>()
                .map { drinkRepository.toggleFavoriteFor(it.alias) }
                .map { ToggleFavoriteOutput(it) },
            filterIsInstance<TogglePlaying>()
                .map { !state().isPlaying }
                .map { TogglePlayingOutput(it) },
        )
    }

    override val reducer: Reducer<DrinkState, DrinkOutput> = { result ->
        when (result) {
            is DrinkWithRelatedOutput -> copy(drinkWithRelated = result.data)
            is ToggleFavoriteOutput -> this
            is TogglePlayingOutput -> copy(isPlaying = result.data)
        }
    }
}