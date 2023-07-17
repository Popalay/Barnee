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

package com.popalay.barnee.ui.screen.shaketodrink

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.SrcAtop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.drinkitem.DrinkItemAction
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateToAction
import com.popalay.barnee.domain.navigation.ParcelableScreen
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkAction
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkState
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkStateMachine
import com.popalay.barnee.ui.common.AnimatedHeartButton
import com.popalay.barnee.ui.common.AsyncImage
import com.popalay.barnee.ui.common.Dialog
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.screen.drink.CollectionBanner
import com.popalay.barnee.ui.theme.DefaultAspectRatio
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.util.asStateFlow
import com.popalay.barnee.util.calories
import com.popalay.barnee.util.collection
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.displayRatingWithMax
import com.popalay.barnee.util.inCollections
import com.popalay.barnee.util.toMinimumData
import io.matthewnelson.component.parcelize.Parcelize

@Parcelize
class ShakeToDrinkScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<ShakeToDrinkStateMachine>()
        val drinkItemStateMachine = injectStateMachine<DrinkItemStateMachine>()
        val state by stateMachine.stateFlow.asStateFlow().collectAsState()
        ShakeToDrinkScreen(state, stateMachine::dispatch, drinkItemStateMachine::dispatch)
    }
}

@Composable
private fun ShakeToDrinkScreen(
    state: ShakeToDrinkState,
    onAction: (Action) -> Unit,
    onItemAction: (Action) -> Unit
) {
    if (state.shouldShow) {
        val hapticFeedback = LocalHapticFeedback.current
        LaunchedEffect(Unit) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }

        Dialog(onDismissRequest = { onAction(ShakeToDrinkAction.DialogDismissed) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Check it out! \uD83D\uDE32\uD83D\uDE0B",
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
                Card(
                    elevation = 4.dp,
                    shape = MediumSquircleShape,
                    modifier = Modifier.aspectRatio(DefaultAspectRatio)
                ) {
                    StateLayout(
                        value = state.randomDrink,
                        loadingState = { LoadingStateView() },
                        errorState = {
                            ErrorAndRetryStateView(
                                onRetry = { onAction(ShakeToDrinkAction.Retry) }
                            )
                        }
                    ) { value ->
                        RandomDrink(
                            data = value,
                            onClick = {
                                onAction(ShakeToDrinkAction.DialogDismissed)
                                onAction(NavigateToAction(AppScreens.Drink(value.toMinimumData())))
                            },
                            onCollectionClick = { onAction(NavigateToAction(AppScreens.SingleCollection(it))) },
                            onHeartClick = { onItemAction(DrinkItemAction.ToggleFavorite(value)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RandomDrink(
    data: Drink,
    onClick: () -> Unit,
    onHeartClick: () -> Unit,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable(onClick = onClick)) {
        AsyncImage(
            imageUrl = data.displayImageUrl,
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = ContentAlpha.disabled), SrcAtop)
        )
        Column(
            modifier = Modifier
                .padding(start = 32.dp, end = 16.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            Text(
                text = data.displayName,
                style = MaterialTheme.typography.h1,
                modifier = Modifier
                    .fillMaxWidth(0.7F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.7F)
            ) {
                Text(
                    text = data.calories,
                    style = MaterialTheme.typography.h3,
                )
                Spacer(modifier = Modifier.width(24.dp))
                CollectionBanner(
                    collection = data.collection,
                    onCollectionClick = onCollectionClick,
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = data.displayRatingWithMax,
                    style = MaterialTheme.typography.h2,
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                )
                AnimatedHeartButton(
                    onToggle = onHeartClick,
                    isSelected = data.inCollections,
                    iconSize = 32.dp,
                )
            }
        }
    }
}
