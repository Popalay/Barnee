package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.screen.collectionlist.CollectionCover
import com.popalay.barnee.ui.screen.collectionlist.CollectionListViewModel
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel

@Composable
fun ChooseCollectionBottomSheet(
    onCollectionClicked: (Collection) -> Unit,
    onCreateNewClicked: () -> Unit
) {
    val viewModel: CollectionListViewModel = getViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

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
            }
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
            modifier = Modifier.aspectRatio(0.8F),
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
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}
