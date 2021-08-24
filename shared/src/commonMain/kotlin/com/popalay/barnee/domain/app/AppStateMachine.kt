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

package com.popalay.barnee.domain.app

import com.popalay.barnee.data.device.ShakeDetector
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.SideEffect
import com.popalay.barnee.domain.State
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.navigation.CheckOutDrinkDestination
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.notification.NotificationAction
import com.popalay.barnee.domain.notification.NotificationService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

object AppState : State

sealed interface AppAction : Action {
    object Initial : AppAction
    data class OnNotificationAction(val action: NotificationAction) : AppAction
}

object AppSideEffect : SideEffect

class AppStateMachine(
    notificationService: NotificationService,
    shakeDetector: ShakeDetector,
    router: Router
) : StateMachine<AppState, AppAction, AppSideEffect>(
    initialState = AppState,
    initialAction = AppAction.Initial,
    reducer = { state, _ ->
        merge(
            filterIsInstance<AppAction.Initial>()
                .take(1)
                .flatMapMerge { detectShakes(shakeDetector) }
                .onEach { router.navigate(CheckOutDrinkDestination) }
                .map { state() },
            filterIsInstance<AppAction.OnNotificationAction>()
                .map { notificationService.handle(it.action) }
                .map { state() },
        )
    }
)

private fun detectShakes(shakeDetector: ShakeDetector) = callbackFlow {
    shakeDetector.start {
        try {
            trySend(true)
        } catch (ignore: Exception) {
            // Handle exception from the channel: failure in flow or premature closing
        }
    }
    awaitClose {
        shakeDetector.stop()
    }
}
