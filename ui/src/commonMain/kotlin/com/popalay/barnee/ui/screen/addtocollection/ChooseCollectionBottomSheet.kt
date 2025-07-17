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

package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.domain.collectionlist.CollectionListState
import com.popalay.barnee.ui.common.BottomSheetContent
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.screen.collectionlist.CollectionCover
import com.popalay.barnee.ui.theme.DefaultAspectRatio
import com.popalay.barnee.ui.theme.MediumSquircleShape

@Composable
fun ChooseCollectionBottomSheet(
    state: CollectionListState,
    onCollectionClicked: (Collection) -> Unit,
    onCreateNewClicked: () -> Unit,
    bottomPadding: PaddingValues,
) {
    StateLayout(state.collections) { value ->
        BottomSheetContent(
            title = { Text(text = "Save to") },
            action = {
                IconButton(onClick = onCreateNewClicked) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create new"
                    )
                }
            },
            body = {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp)) {
                    itemsIndexed(value.toList()) { index, item ->
                        BottomSheetCollectionItem(
                            data = item,
                            onClick = { onCollectionClicked(item) }
                        )
                        if (index != value.size - 1) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            },
            bottomPadding = bottomPadding
        )
    }
}

@Composable
private fun BottomSheetCollectionItem(
    data: Collection,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.width(76.dp)) {
        Card(
            elevation = 4.dp,
            shape = MediumSquircleShape,
            modifier = Modifier.aspectRatio(DefaultAspectRatio),
        ) {
            CollectionCover(
                images = data.cover,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
        Text(
            text = data.name,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        )
    }
}
