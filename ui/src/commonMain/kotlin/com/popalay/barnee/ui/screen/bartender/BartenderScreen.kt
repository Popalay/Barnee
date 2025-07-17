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

package com.popalay.barnee.ui.screen.bartender

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.moriatsushi.insetsx.ExperimentalSoftwareKeyboardApi
import com.moriatsushi.insetsx.ime
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.bartender.BartenderAction
import com.popalay.barnee.domain.bartender.BartenderState
import com.popalay.barnee.domain.bartender.BartenderStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateBackAction
import com.popalay.barnee.domain.navigation.ParcelableScreen
import com.popalay.barnee.domain.navigation.ReplaceCurrentScreenAction
import com.popalay.barnee.ui.common.BarneeTextField
import com.popalay.barnee.ui.common.BottomSheetContent
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.icons.Cross
import com.popalay.barnee.ui.platform.collectAsStateWithLifecycle
import com.popalay.barnee.ui.screen.drinklist.DrinkListItem
import com.popalay.barnee.util.asStateFlow
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.toMinimumData
import io.matthewnelson.component.parcelize.Parcelize

@Parcelize
class BartenderScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<BartenderStateMachine>()
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        BartenderScreen(state, stateMachine::dispatch)
    }
}

@OptIn(ExperimentalSoftwareKeyboardApi::class)
@Composable
private fun BartenderScreen(
    state: BartenderState,
    onAction: (Action) -> Unit
) {
    Crossfade(targetState = state.generatedDrink, label = "bartender-content") { drink ->
        if (drink != null) {
            BottomSheetContent(
                title = { Text(text = "Welcome ${drink.displayName}!") },
                body = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DrinkListItem(
                            data = drink,
                            onClick = { onAction(ReplaceCurrentScreenAction(AppScreens.Drink(drink.toMinimumData()))) },
                            modifier = Modifier.fillMaxWidth(0.5F)
                        )
                    }
                },
                navigation = {
                    IconButton(onClick = { onAction(NavigateBackAction) }) {
                        Icon(
                            imageVector = Icons.Cross,
                            tint = MaterialTheme.colors.onSurface,
                            contentDescription = "Close"
                        )
                    }
                },
                bottomPadding = WindowInsets.ime.asPaddingValues()
            )
        } else {
            val promptFocus = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                promptFocus.requestFocus()
            }

            BottomSheetContent(
                title = { Text(text = "Let's shake!") },
                action = {
                    IconButton(
                        onClick = { onAction(BartenderAction.OnGenerateDrinkClicked) },
                        enabled = state.isPromptValid && !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (state.isError) Icons.Default.Refresh else Icons.Default.Done,
                                contentDescription = "Shake"
                            )
                        }
                    }
                },
                body = {
                    Column {
                        BarneeTextField(
                            value = state.prompt,
                            onValueChange = { onAction(BartenderAction.OnPromptChanged(it)) },
                            label = { Text(text = "Explain what you want") },
                            placeholder = { Text(text = "e.g. sour cocktail with gin and cherry") },
                            isError = state.isError,
                            enabled = !state.isLoading,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.body1,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Unspecified,
                                focusedIndicatorColor = Color.Unspecified,
                                unfocusedIndicatorColor = Color.Unspecified,
                                disabledIndicatorColor = Color.Unspecified,
                                errorIndicatorColor = Color.Unspecified,
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = if (state.isPromptValid) ImeAction.Done else ImeAction.None
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (state.isPromptValid) {
                                        onAction(BartenderAction.OnGenerateDrinkClicked)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(promptFocus)
                        )
                        if (state.isError) {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                },
                navigation = {
                    IconButton(onClick = { onAction(NavigateBackAction) }) {
                        Icon(
                            imageVector = Icons.Cross,
                            tint = MaterialTheme.colors.onSurface,
                            contentDescription = "Close"
                        )
                    }
                },
                bottomPadding = WindowInsets.ime.asPaddingValues()
            )
        }
    }
}
