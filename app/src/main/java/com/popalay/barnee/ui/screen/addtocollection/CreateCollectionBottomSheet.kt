package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl
import com.popalay.barnee.util.displayImageUrl

@Composable
fun CreateCollectionBottomSheet(
    drink: Drink,
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
                        .aspectRatio(0.8F)
                ) {
                    Image(
                        painter = rememberCoilPainter(
                            request = drink.displayImageUrl,
                            requestBuilder = { size -> applyForImageUrl(drink.displayImageUrl, size) },
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                TextField(
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
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
        }
    )
}
