package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.Receipt
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.drink.DrinkAction.Initial
import com.popalay.barnee.domain.drink.DrinkAction.ToggleFavorite

data class DrinkState(
    val receipt: Result<Receipt> = Uninitialized()
) : State

sealed class DrinkAction : Action {
    data class Initial(val alias: String) : DrinkAction()
    data class ToggleFavorite(val alias: String) : DrinkAction()
}

class DrinkStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction>(DrinkState()) {
    override fun reducer(currentState: DrinkState, action: DrinkAction) {
        when (action) {
            is Initial -> drinkRepository.getReceipt(action.alias).execute { copy(receipt = it) }
            is ToggleFavorite -> suspend { drinkRepository.toggleFavoriteFor(action.alias) }.execute()
        }
    }
}