package com.popalay.barnee.domain.shakedrink

import com.popalay.barnee.data.device.ShakeDetector
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Mutation
import com.popalay.barnee.domain.Processor
import com.popalay.barnee.domain.Reducer
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkAction.DialogDismissed
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkAction.Initial
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkMutation.RandomDrinkMutation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

data class ShakeToDrinkState(
    val randomDrink: Result<Drink> = Uninitialized(),
    val shouldShow: Boolean = false
) : State

sealed class ShakeToDrinkAction : Action {
    object Initial : ShakeToDrinkAction()
    object DialogDismissed : ShakeToDrinkAction()
}

sealed class ShakeToDrinkMutation : Mutation {
    data class RandomDrinkMutation(val data: Result<Drink>) : ShakeToDrinkMutation()
}

class ShakeToDrinkStateMachine(
    private val drinkRepository: DrinkRepository,
    private val shakeDetector: ShakeDetector
) : StateMachine<ShakeToDrinkState, ShakeToDrinkAction, ShakeToDrinkMutation>(ShakeToDrinkState(), Initial) {
    override val processor: Processor<ShakeToDrinkState, ShakeToDrinkMutation> = {
        merge(
            filterIsInstance<Initial>()
                .take(1)
                .flatMapMerge { detectShakes() }
                .flatMapToResult {
                    drinkRepository.getDrinks(DrinksRequest.Random(1))
                        .map { it.first() }
                        .take(1)
                }
                .map { RandomDrinkMutation(it) },
            filterIsInstance<DialogDismissed>()
                .map { RandomDrinkMutation(Uninitialized()) },
        )
    }

    override val reducer: Reducer<ShakeToDrinkState, ShakeToDrinkMutation> = { mutation ->
        when (mutation) {
            is RandomDrinkMutation -> copy(
                randomDrink = mutation.data,
                shouldShow = mutation.data !is Uninitialized
            )
        }
    }

    private fun detectShakes() = callbackFlow {
        shakeDetector.start {
            try {
                offer(true)
            } catch (e: Exception) {
                // Handle exception from the channel: failure in flow or premature closing
            }
        }
        awaitClose {
            shakeDetector.stop()
        }
    }
}