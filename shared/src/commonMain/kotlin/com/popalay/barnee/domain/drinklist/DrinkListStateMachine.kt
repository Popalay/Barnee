package com.popalay.barnee.domain.drinklist

import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Output
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.drinklist.DrinkListAction.ToggleFavorite
import com.popalay.barnee.domain.drinklist.DrinkListOutput.ToggleFavoriteOutput
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class DrinkListState(
    val isFavorite: Boolean = false
) : State

sealed class DrinkListAction : Action {
    data class ToggleFavorite(val alias: String) : DrinkListAction()
}

sealed class DrinkListOutput : Output {
    data class ToggleFavoriteOutput(val data: Boolean) : DrinkListOutput()
}

class DrinkListStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkListState, DrinkListAction, DrinkListOutput>(DrinkListState()) {
    override val processor: Processor<DrinkListState, DrinkListOutput> = {
        merge(
            filterIsInstance<ToggleFavorite>()
                .map { drinkRepository.toggleFavoriteFor(it.alias) }
                .map { ToggleFavoriteOutput(it) },
        )
    }

    override val reducer: Reducer<DrinkListState, DrinkListOutput> = { result ->
        when (result) {
            is ToggleFavoriteOutput -> copy(isFavorite = result.data)
        }
    }
}