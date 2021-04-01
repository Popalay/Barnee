package com.popalay.barnee.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells.Fixed
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign.Start
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.google.accompanist.coil.CoilImage
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.drinklist.DrinkListAction
import com.popalay.barnee.ui.screen.drinklist.DrinkListViewModel
import com.popalay.barnee.ui.screen.navigation.Screen
import com.popalay.barnee.ui.theme.LightGrey
import org.koin.androidx.compose.getViewModel
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrinkList(
    drinks: Result<List<Drink>>,
    navController: NavController,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: DrinkListViewModel = getViewModel()

    StateLayout(
        value = drinks,
        emptyState = { EmptyStateView(message = emptyMessage) },
        errorState = { EmptyStateView(message = emptyMessage) },
        loadingState = { LoadingStateView() }
    ) { value ->
        LazyVerticalGrid(
            cells = Fixed(2),
            contentPadding = PaddingValues(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding())) }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding())) }
            itemsIndexed(value) { index, item ->
                DrinkListItem(
                    item,
                    onClick = { navController.navigate(Screen.Drink(item.alias, item.name, item.displayImageUrl).route) },
                    onDoubleClick = { viewModel.processAction(DrinkListAction.ToggleFavorite(item.alias)) },
                    onHeartClick = { viewModel.processAction(DrinkListAction.ToggleFavorite(item.alias)) },
                    modifier = modifier
                        .padding(start = 12.dp, end = 12.dp, bottom = 0.dp)
                        .padding(top = if (index % 2 == 0 && value.size > 1) 24.dp else 0.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrinkListItem(
    data: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
    onHeartClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(0.8F)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onDoubleClick = onDoubleClick)
    ) {
        CoilImage(
            data = data.displayImageUrl,
            fadeIn = true,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            loading = { Box(modifier = Modifier.background(LightGrey)) },
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = buildAnnotatedString {
                append(data.name.toLowerCase(Locale.getDefault()))
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colors.primary,
                        fontStyle = MaterialTheme.typography.h3.fontStyle,
                        fontFamily = MaterialTheme.typography.h3.fontFamily,
                        fontWeight = MaterialTheme.typography.h3.fontWeight,
                        fontSize = MaterialTheme.typography.h3.fontSize
                    )
                ) {
                    append("  " + (data.rating / 10F).toString())
                }
            },
            style = MaterialTheme.typography.h2,
            textAlign = Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        0.1f to Color.Transparent,
                        0.7f to Color.Black.copy(alpha = 0.5F)
                    )
                )
                .padding(16.dp)
                .align(Alignment.BottomStart)
        )
        IconButton(
            onClick = onHeartClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = if (data.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart),
                contentDescription = "Like"
            )
        }
    }
}