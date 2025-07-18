/*
 * Copyright (c) 2025 Denys Nykyforov
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

package com.popalay.barnee.domain.drinkitem

import com.popalay.barnee.data.message.Message
import com.popalay.barnee.data.message.MessagesProvider
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.NoState
import com.popalay.barnee.domain.StateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.util.capitalizeFirstChar
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.inCollections
import com.popalay.barnee.util.toMinimumData
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

sealed interface DrinkItemAction : Action {
    data class ToggleFavorite(val drink: Drink) : DrinkItemAction
}

class DrinkItemStateMachine(
    collectionRepository: CollectionRepository,
    messagesProvider: MessagesProvider,
) : StateMachine<NoState>(
    initialState = NoState,
    reducer = { state, _ ->
        merge(
            filterIsInstance<DrinkItemAction.ToggleFavorite>()
                .map {
                    if (it.drink.inCollections) {
                        collectionRepository.removeFromAllCollections(it.drink.toMinimumData())
                    } else {
                        collectionRepository.addToCollection(drink = it.drink.toMinimumData())
                        messagesProvider.dispatch(
                            Message.SnackBar(
                                content = "${it.drink.displayName.capitalizeFirstChar()} was added to favorites",
                                action = Message.SnackBar.Action(
                                    text = "Change",
                                    destination = AppScreens.AddToCollection(it.drink.toMinimumData()),
                                )
                            )
                        )
                    }
                }
                .map { state() },
        )
    }
)
