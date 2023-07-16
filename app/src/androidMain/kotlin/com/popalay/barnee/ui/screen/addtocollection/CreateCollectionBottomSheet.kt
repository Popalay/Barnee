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

package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.ui.common.AsyncImage
import com.popalay.barnee.ui.common.BarneeTextField
import com.popalay.barnee.ui.common.BottomSheetContent
import com.popalay.barnee.ui.icons.ChevronLeft
import com.popalay.barnee.ui.theme.DefaultAspectRatio
import com.popalay.barnee.ui.theme.MediumSquircleShape

@Composable
fun CreateCollectionBottomSheet(
    drink: DrinkMinimumData,
    collectionName: String,
    canBeSaved: Boolean,
    onSaveClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onCollectionNameChanged: (String) -> Unit
) {
    val nameFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        nameFocus.requestFocus()
    }

    BottomSheetContent(
        title = { Text(text = "New collection") },
        action = {
            IconButton(
                onClick = onSaveClicked,
                enabled = canBeSaved
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Save"
                )
            }
        },
        body = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    elevation = 4.dp,
                    shape = MediumSquircleShape,
                    modifier = Modifier
                        .height(96.dp)
                        .aspectRatio(DefaultAspectRatio)
                ) {
                    AsyncImage(drink.displayImageUrl)
                }
                BarneeTextField(
                    value = collectionName,
                    onValueChange = onCollectionNameChanged,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.body1.copy(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Unspecified),
                    modifier = Modifier
                        .fillMaxWidth(0.3F)
                        .focusRequester(nameFocus)
                )
            }
        },
        navigation = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.ChevronLeft,
                    tint = MaterialTheme.colors.onSurface,
                    contentDescription = "Back"
                )
            }
        },
        bottomPadding = WindowInsets.ime.asPaddingValues()
    )
}
