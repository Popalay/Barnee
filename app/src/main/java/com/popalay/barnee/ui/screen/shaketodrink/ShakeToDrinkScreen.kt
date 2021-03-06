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

package com.popalay.barnee.ui.screen.shaketodrink

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.SrcAtop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.drinkitem.DrinkItemAction
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkAction
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkState
import com.popalay.barnee.ui.common.AnimatedHeartButton
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.screen.drink.CollectionBanner
import com.popalay.barnee.ui.screen.drinklist.DrinkItemViewModel
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.theme.DEFAULT_ASPECT_RATIO
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl
import com.popalay.barnee.ui.util.collectAsStateWithLifecycle
import com.popalay.barnee.ui.util.toIntSize
import com.popalay.barnee.util.calories
import com.popalay.barnee.util.collection
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.displayRatingWithMax
import com.popalay.barnee.util.inCollections
import org.koin.androidx.compose.getViewModel

@Composable
fun ShakeToDrinkScreen() {
    ShakeToDrinkScreen(getViewModel(), getViewModel())
}

@Composable
fun ShakeToDrinkScreen(viewModel: ShakeToDrinkViewModel, drinkItemViewModel: DrinkItemViewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    ShakeToDrinkScreen(state, viewModel::processAction, drinkItemViewModel::processAction)
}

@Composable
fun ShakeToDrinkScreen(
    state: ShakeToDrinkState,
    onAction: (ShakeToDrinkAction) -> Unit,
    onItemAction: (DrinkItemAction) -> Unit
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
                    modifier = Modifier.aspectRatio(DEFAULT_ASPECT_RATIO)
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
                                onItemAction(DrinkItemAction.DrinkClicked(value))
                            },
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
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.clickable(onClick = onClick)) {
        Image(
            painter = rememberImagePainter(
                data = data.displayImageUrl,
                builder = { applyForImageUrl(data.displayImageUrl, constraints.toIntSize()) },
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
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
                CollectionBanner(data.collection)
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

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ShakeToDrinkScreenPreview() {
    BarneeTheme {
        ShakeToDrinkScreen()
    }
}
