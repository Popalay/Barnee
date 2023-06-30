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

package com.popalay.barnee.ui.screen.bartender

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.popalay.barnee.R
import com.popalay.barnee.domain.bartender.BartenderAction
import com.popalay.barnee.domain.bartender.BartenderState
import com.popalay.barnee.ui.common.BarneeTextField
import com.popalay.barnee.ui.screen.addtocollection.BottomSheetContent
import org.koin.androidx.compose.getViewModel

@Composable
fun BartenderScreen() {
    BartenderScreen(getViewModel())
}

@Composable
fun BartenderScreen(viewModel: BartenderViewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    BartenderScreen(state, viewModel::dispatchAction)
}

@Composable
fun BartenderScreen(
    state: BartenderState,
    onAction: (BartenderAction) -> Unit
) {
    BartenderBottomSheet(
        prompt = state.prompt,
        isError = state.isError,
        isLoading = state.isLoading,
        canBeGenerated = state.isPromptValid,
        onGenerateClicked = { onAction(BartenderAction.OnGenerateDrinkClicked) },
        onBackClicked = { onAction(BartenderAction.OnCloseClicked) },
        onPromptChanged = { onAction(BartenderAction.OnPromptChanged(it)) }
    )
}

@Composable
private fun BartenderBottomSheet(
    prompt: String,
    isError: Boolean,
    isLoading: Boolean,
    onBackClicked: () -> Unit,
    onGenerateClicked: () -> Unit,
    canBeGenerated: Boolean,
    onPromptChanged: (String) -> Unit
) {
    val promptFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        promptFocus.requestFocus()
    }

    BottomSheetContent(
        title = { Text(text = "Let's shake!") },
        action = {
            IconButton(
                onClick = onGenerateClicked,
                enabled = canBeGenerated && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (isError) Icons.Default.Refresh else Icons.Default.Done,
                        contentDescription = "Shake"
                    )
                }
            }
        },
        body = {
            BarneeTextField(
                value = prompt,
                onValueChange = onPromptChanged,
                label = { Text(text = "Explain what you want") },
                placeholder = { Text(text = "e.g. sour cocktail with gin and cherry") },
                isError = isError,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Unspecified,
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(promptFocus)
            )
        },
        navigation = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_cross),
                    contentDescription = "Close"
                )
            }
        }
    )
}
