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

package com.popalay.barnee.ui.screen.drink

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.data.model.Ingredient
import com.popalay.barnee.data.model.Instruction
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.drink.DrinkAction
import com.popalay.barnee.domain.drink.DrinkInput
import com.popalay.barnee.domain.drink.DrinkState
import com.popalay.barnee.domain.drinkitem.DrinkItemAction
import com.popalay.barnee.domain.navigation.CollectionDestination
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.ui.common.AnimatedHeartButton
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.CollapsingScaffold
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.YouTubePlayer
import com.popalay.barnee.ui.common.rememberCollapsingScaffoldState
import com.popalay.barnee.ui.screen.drinklist.DrinkHorizontalList
import com.popalay.barnee.ui.screen.drinklist.DrinkItemViewModel
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.theme.LightGrey
import com.popalay.barnee.ui.theme.SquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl
import com.popalay.barnee.ui.util.collectAsStateWithLifecycle
import com.popalay.barnee.util.calories
import com.popalay.barnee.util.displayRatingWithMax
import com.popalay.barnee.util.displayStory
import com.popalay.barnee.util.displayText
import com.popalay.barnee.util.inCollection
import com.popalay.barnee.util.isDefault
import com.popalay.barnee.util.keywords
import com.popalay.barnee.util.toImageUrl
import com.popalay.barnee.util.videoUrl
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

@Composable
fun DrinkScreen(input: DrinkInput) {
    DrinkScreen(getViewModel { parametersOf(input) }, getViewModel())
}

@Composable
fun DrinkScreen(viewModel: DrinkViewModel, drinkItemViewModel: DrinkItemViewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    DrinkScreen(state, viewModel::processAction, drinkItemViewModel::processAction)
}

@Composable
fun DrinkScreen(
    state: DrinkState,
    onAction: (DrinkAction) -> Unit,
    onItemAction: (DrinkItemAction) -> Unit
) {
    val drink by derivedStateOf { state.drinkWithRelated()?.drink }
    val screenWidthDp = with(LocalConfiguration.current) { remember(this) { screenWidthDp.dp } }
    val toolbarHeightPx = with(LocalDensity.current) { (screenWidthDp / 0.8F).toPx() }
    val collapsedToolbarHeightPx = with(LocalDensity.current) { 88.dp.toPx() + LocalWindowInsets.current.statusBars.bottom }
    val listState = rememberLazyListState()
    val collapsingScaffoldState = rememberCollapsingScaffoldState(
        minHeight = collapsedToolbarHeightPx,
        maxHeight = toolbarHeightPx,
        confirmOffsetChange = { _, _ -> drink != null && listState.firstVisibleItemScrollOffset == 0 }
    )

    CollapsingScaffold(
        state = collapsingScaffoldState,
        topBar = {
            val secondaryElementsAlpha by derivedStateOf { if (state.isPlaying) 0F else 1 - collapsingScaffoldState.fraction * 1.5F }
            val offset by derivedStateOf { IntOffset(0, collapsingScaffoldState.offset.value.roundToInt()) }

            DrinkAppBar(
                isPlaying = state.isPlaying,
                imageContent = {
                    ImageContent(
                        image = state.displayImage,
                        rating = drink?.displayRatingWithMax.orEmpty(),
                        showPlayButton = !drink?.videoUrl.isNullOrBlank(),
                        isHeartButtonSelected = drink?.inCollection,
                        secondaryElementsAlpha = secondaryElementsAlpha,
                        onHeartClick = { drink?.let { onItemAction(DrinkItemAction.ToggleFavorite(it)) } },
                        onPlayClick = { onAction(DrinkAction.TogglePlaying) }
                    )
                },
                videoContent = {
                    YouTubePlayer(
                        uri = drink?.videoUrl.orEmpty(),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(bottom = 16.dp)
                    )
                },
                sharedContent = {
                    SharedContent(
                        title = state.displayName,
                        nutrition = drink?.calories.orEmpty(),
                        collection = drink?.collection,
                        isPlaying = state.isPlaying,
                        shouldCutTitle = !drink?.videoUrl.isNullOrBlank(),
                        secondaryElementsAlpha = secondaryElementsAlpha,
                        offset = offset,
                        scrollFraction = collapsingScaffoldState.fraction,
                        onShareClick = { onAction(DrinkAction.ShareClicked) }
                    )
                },
                scrollFraction = collapsingScaffoldState.fraction,
                modifier = Modifier.offset { offset }
            )
        }
    ) { contentPadding ->
        DrinkScreenBody(
            drinkWithRelated = state.drinkWithRelated,
            listState = listState,
            contentPadding = contentPadding,
            onRetryClicked = { onAction(DrinkAction.Retry) },
            onCategoryClicked = { onAction(DrinkAction.CategoryClicked(it)) },
            onMoreRecommendedDrinksClicked = { onAction(DrinkAction.MoreRecommendedDrinksClicked) },
        )
    }
}

@Composable
fun DrinkScreenBody(
    drinkWithRelated: Result<FullDrinkResponse>,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onRetryClicked: () -> Unit,
    onCategoryClicked: (Category) -> Unit,
    onMoreRecommendedDrinksClicked: () -> Unit,
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding
    ) {
        item {
            StateLayout(
                value = drinkWithRelated,
                loadingState = {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .offset(y = (-26).dp)
                            .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                    )
                },
                errorState = {
                    ErrorAndRetryStateView(
                        onRetry = onRetryClicked,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            ) { value ->
                Column {
                    Spacer(modifier = Modifier.height(32.dp))
                    if (value.drink.displayStory.isNotBlank()) {
                        Story(
                            story = value.drink.displayStory,
                            modifier = Modifier.padding(start = 32.dp, end = 24.dp)
                        )
                        Divider(modifier = Modifier.padding(vertical = 24.dp))
                    }
                    Ingredients(
                        ingredient = value.drink.ingredients,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 24.dp))
                    Steps(
                        instruction = value.drink.instruction,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 24.dp))
                    if (value.drink.keywords.isNotEmpty()) {
                        Keywords(
                            keywords = value.drink.keywords,
                            onClick = { onCategoryClicked(it) },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Divider(modifier = Modifier.padding(vertical = 24.dp))
                    }
                    RecommendedDrinks(
                        data = value.relatedDrinks,
                        onShowMoreClick = onMoreRecommendedDrinksClicked,
                    )
                    Spacer(modifier = Modifier.navigationBarsHeight(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DrinkAppBar(
    isPlaying: Boolean,
    videoContent: @Composable BoxScope.() -> Unit,
    imageContent: @Composable BoxScope.() -> Unit,
    sharedContent: @Composable BoxScope.() -> Unit,
    scrollFraction: Float,
    modifier: Modifier = Modifier,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Card(
        elevation = 4.dp,
        shape = SquircleShape(
            curveTopStart = 0F,
            curveTopEnd = 0F,
            curveBottomStart = 0.1F * (1 - scrollFraction),
            curveBottomEnd = 0.1F * (1 - scrollFraction)
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLandscape) Modifier.fillMaxHeight() else Modifier.aspectRatio(0.8F))
    ) {
        Box {
            Crossfade(isPlaying) {
                if (it) {
                    videoContent()
                } else {
                    imageContent()
                }
            }
            sharedContent()
        }
    }
}

@Composable
private fun SharedContent(
    title: String,
    nutrition: String,
    collection: Collection?,
    isPlaying: Boolean,
    shouldCutTitle: Boolean,
    offset: IntOffset,
    scrollFraction: Float,
    secondaryElementsAlpha: Float,
    onShareClick: () -> Unit
) {
    val titleTextSize = remember(scrollFraction) { (56 * (1 - scrollFraction)).coerceAtLeast(24F) }
    val titleMaxLines = remember(scrollFraction) { if (scrollFraction > 0.9F) 1 else if (shouldCutTitle) 3 else 6 }
    val titleAlpha = if (isPlaying) scrollFraction * 1.5F else 1F
    val titleOffset = with(LocalDensity.current) {
        IntOffset(
            x = (28.dp.toPx() * scrollFraction).toInt(),
            y = ((-40).dp.toPx() * scrollFraction).toInt()
        ) - offset
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(scrollFraction)
            .background(MaterialTheme.colors.background)
    )
    Column(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 16.dp)
                .offset { -offset }
        ) {
            BackButton()
            Spacer(modifier = Modifier.fillMaxWidth().weight(1F))
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share drink",
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.h1.copy(fontSize = titleTextSize.sp),
            maxLines = titleMaxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 24.dp)
                .fillMaxWidth(0.7F)
                .offset { titleOffset }
                .alpha(titleAlpha)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 24.dp)
                .fillMaxWidth(0.7F)
                .alpha(secondaryElementsAlpha)
        ) {
            Text(
                text = nutrition,
                style = MaterialTheme.typography.h3,
            )
            Spacer(modifier = Modifier.width(24.dp))
            CollectionBanner(collection)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CollectionBanner(
    collection: Collection?,
    modifier: Modifier = Modifier
) {
    val router: Router = get()
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = collection?.isDefault == false,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.medium),
            modifier = Modifier
                .clip(CircleShape)
                .clickable { scope.launch { router.navigate(CollectionDestination(requireNotNull(collection))) } }
        ) {
            Text(
                text = collection?.name.orEmpty(),
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun BoxScope.ImageContent(
    image: ImageUrl,
    rating: String,
    showPlayButton: Boolean,
    isHeartButtonSelected: Boolean?,
    secondaryElementsAlpha: Float,
    onPlayClick: () -> Unit,
    onHeartClick: () -> Unit
) {
    Image(
        painter = rememberCoilPainter(
            request = image,
            requestBuilder = { size -> applyForImageUrl(image, size) },
            fadeIn = true
        ),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = ContentAlpha.disabled), BlendMode.SrcAtop)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .padding(start = 32.dp, end = 24.dp, bottom = 8.dp)
            .alpha(secondaryElementsAlpha)
    ) {
        Text(
            text = rating,
            style = MaterialTheme.typography.h2,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        AnimatedVisibility(isHeartButtonSelected != null) {
            AnimatedHeartButton(
                onToggle = onHeartClick,
                isSelected = isHeartButtonSelected == true,
                iconSize = 32.dp,
            )
        }
    }
    if (showPlayButton) {
        PlayButton(
            onClick = onPlayClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .alpha(secondaryElementsAlpha)
        )
    }
}

@Composable
private fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(LocalContentColor.current.copy(alpha = ContentAlpha.disabled))
            .size(72.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun Ingredients(
    ingredient: List<Ingredient>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ingredient.forEachIndexed { index, item ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.body1,
                color = LightGrey
            )
        }
    }
}

@Composable
private fun Story(
    story: String,
    modifier: Modifier = Modifier
) {
    var isTextCollapsed by rememberSaveable { mutableStateOf(true) }
    val textLength by animateIntAsState(if (isTextCollapsed) 100 else story.length)
    val arrowRotation by animateIntAsState(if (isTextCollapsed) 270 else 90)

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Story",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            )
            TextButton(
                onClick = { isTextCollapsed = !isTextCollapsed },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onBackground)
            ) {
                Text(
                    text = if (isTextCollapsed) "More" else "Less",
                    style = MaterialTheme.typography.subtitle2,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = if (isTextCollapsed) "More" else "Less",
                    modifier = Modifier.size(8.dp).rotate(arrowRotation.toFloat())
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = story.take(textLength) + if (isTextCollapsed) "..." else "",
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun Steps(
    instruction: Instruction,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Steps",
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.height(16.dp))
        instruction.steps.forEachIndexed { index, item ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (index + 1).toString().padStart(2, '0'),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
                )
            }
        }
    }
}

@Composable
private fun Keywords(
    keywords: List<Category>,
    onClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        FlowRow {
            keywords.forEach { item ->
                Text(
                    text = item.displayText,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onClick(item) }
                        .padding(vertical = 4.dp)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun RecommendedDrinks(
    data: List<Drink>,
    onShowMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 32.dp, end = 24.dp)
        ) {
            Text(
                text = "Recommended",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            )
            TextButton(
                onClick = onShowMoreClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onBackground)
            ) {
                Text(
                    text = "More",
                    style = MaterialTheme.typography.subtitle2,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "More",
                    modifier = Modifier.size(8.dp).rotate(180F)
                )
            }
        }
        DrinkHorizontalList(
            data = data,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DrinkScreenPreview() {
    BarneeTheme {
        DrinkScreen(DrinkInput("alias", "name", "sample.png".toImageUrl()))
    }
}
