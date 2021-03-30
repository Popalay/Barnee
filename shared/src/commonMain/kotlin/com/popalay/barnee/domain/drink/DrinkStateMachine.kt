package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.model.Receipt
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
import com.popalay.barnee.domain.drink.DrinkOutput.ReceiptResult
import com.popalay.barnee.domain.drink.DrinkOutput.ToggleFavoriteOutput
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class DrinkState(
    val receipt: Result<Receipt> = Uninitialized()
) : State

sealed class DrinkAction : Action {
    data class Initial(val alias: String) : DrinkAction()
    data class ToggleFavorite(val alias: String) : DrinkAction()
}

sealed class DrinkOutput : Output {
    data class ReceiptResult(val data: Result<Receipt>) : DrinkOutput()
    data class ToggleFavoriteOutput(val data: Boolean) : DrinkOutput()
}

class DrinkStateMachine(
    private val drinkRepository: DrinkRepository
) : StateMachine<DrinkState, DrinkAction, DrinkOutput>(DrinkState()) {
    override val processor: Processor<DrinkState, DrinkOutput> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapToResult { drinkRepository.getReceipt(it.alias) }
                .map { ReceiptResult(it) },
            filterIsInstance<ToggleFavorite>()
                .map { drinkRepository.toggleFavoriteFor(it.alias) }
                .map { ToggleFavoriteOutput(it) },
        )
    }

    override val reducer: Reducer<DrinkState, DrinkOutput> = { result ->
        when (result) {
            is ReceiptResult -> copy(receipt = result.data)
            is ToggleFavoriteOutput -> this
        }
    }
}