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

package com.popalay.barnee.ui.screen.housebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll

@Composable
fun HouseBarScreen() {
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            HouseBarAppBar(
                modifier = Modifier.liftOnScroll(listState)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
//        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                text = { Text(text = "Shake cocktail") },
//                icon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_cocktail_shaker),
//                        contentDescription = "Shake cocktail"
//                    )
//                },
//                interactionSource = remember { MutableInteractionSource() },
//                backgroundColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
//                onClick = { /*TODO*/ },
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//        },
    ) { innerPadding ->
        HouseBarComponents(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun HouseBarAppBar(
    modifier: Modifier = Modifier,
) {
    ActionsAppBar(
        title = "House Bar",
        modifier = modifier,
        leadingButtons = { BackButton() },
    )
}

@Composable
private fun HouseBarComponents(
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(32.dp)
            .fillMaxSize()
    ) {
        Text(
            style = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)),
            textAlign = TextAlign.Center,
            text = "House bar and AI generated cocktails will be available soon.\nStay tuned! \uD83C\uDF78\uD83D\uDCA5"
        )
    }
}
