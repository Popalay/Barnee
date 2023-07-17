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

package com.popalay.barnee.domain.drink

import com.popalay.barnee.data.device.KeepScreenOnSetter
import com.popalay.barnee.data.message.Message
import com.popalay.barnee.data.message.MessagesProvider
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.ShareRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.InitialAction
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.flatMapToResult
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.identifier
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

data class DrinkState(
    val drinkMinimumData: DrinkMinimumData,
    val drinkWithRelated: Result<FullDrinkResponse> = Uninitialized(),
    val isScreenKeptOn: Boolean = false,
    val isPlaying: Boolean = false
) : State {
    val identifier = drinkWithRelated()?.drink?.identifier ?: drinkMinimumData.identifier
    val displayName = drinkWithRelated()?.drink?.displayName ?: drinkMinimumData.name
    val displayImage = drinkWithRelated()?.drink?.displayImageUrl ?: drinkMinimumData.displayImageUrl
}

sealed interface DrinkAction : Action {
    object TogglePlaying : DrinkAction
    object Retry : DrinkAction
    object ShareClicked : DrinkAction
    object KeepScreenOnClicked : DrinkAction
}

class DrinkStateMachine(
    drinkMinimumData: DrinkMinimumData,
    drinkRepository: DrinkRepository,
    shareRepository: ShareRepository,
    messagesProvider: MessagesProvider,
    private val keepScreenOnSetter: KeepScreenOnSetter,
) : StateMachine<DrinkState>(
    initialState = DrinkState(drinkMinimumData),
    reducer = { state, _ ->
        merge(
            filterIsInstance<InitialAction>()
                .take(1)
                .flatMapToResult { drinkRepository.fullDrink(state().identifier) }
                .map { state().copy(drinkWithRelated = it) },
            filterIsInstance<DrinkAction.Retry>()
                .flatMapToResult { drinkRepository.fullDrink(state().identifier) }
                .map { state().copy(drinkWithRelated = it) },
            filterIsInstance<DrinkAction.TogglePlaying>()
                .map { !state().isPlaying }
                .map { state().copy(isPlaying = it) },
            filterIsInstance<DrinkAction.ShareClicked>()
                .onEach { shareRepository.shareDrink(requireNotNull(state().drinkWithRelated()?.drink)) }
                .map { state() },
            filterIsInstance<DrinkAction.KeepScreenOnClicked>()
                .map { !state().isScreenKeptOn }
                .onEach { keepScreenOn ->
                    val toast = if (keepScreenOn) {
                        keepScreenOnSetter.on()
                        Message.SnackBar("The screen will remain on on this screen")
                    } else {
                        keepScreenOnSetter.off()
                        Message.SnackBar("The screen will turn off as usual")
                    }
                    messagesProvider.dispatch(toast)
                }
                .map { state().copy(isScreenKeptOn = it) },
        )
    }
) {
    override fun onDispose() {
        keepScreenOnSetter.off()
    }
}
