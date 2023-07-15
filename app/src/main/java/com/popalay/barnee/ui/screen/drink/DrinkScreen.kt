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

package com.popalay.barnee.ui.screen.drink

import android.content.res.Configuration
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.model.Ingredient
import com.popalay.barnee.data.model.Instruction
import com.popalay.barnee.di.AsyncImage
import com.popalay.barnee.di.injectStateMachine
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.Success
import com.popalay.barnee.domain.drink.DrinkAction
import com.popalay.barnee.domain.drink.DrinkState
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinkitem.DrinkItemAction
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateBackAction
import com.popalay.barnee.domain.navigation.NavigateToAction
import com.popalay.barnee.domain.navigation.ScreenWithInputAsKey
import com.popalay.barnee.domain.navigation.ScreenWithTransition
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.ActionsAppBarHeight
import com.popalay.barnee.ui.common.AnimatedHeartButton
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.CollapsingScaffold
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.YouTubePlayer
import com.popalay.barnee.ui.common.rememberCollapsingScaffoldState
import com.popalay.barnee.ui.screen.drinklist.DrinkHorizontalList
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.theme.DefaultAspectRatio
import com.popalay.barnee.ui.theme.LightGrey
import com.popalay.barnee.ui.theme.SquircleShape
import com.popalay.barnee.ui.util.findActivity
import com.popalay.barnee.util.asStateFlow
import com.popalay.barnee.util.calories
import com.popalay.barnee.util.collection
import com.popalay.barnee.util.displayRatingWithMax
import com.popalay.barnee.util.displayStory
import com.popalay.barnee.util.displayText
import com.popalay.barnee.util.isDefault
import com.popalay.barnee.util.isGenerated
import com.popalay.barnee.util.keywords
import com.popalay.barnee.util.toImageUrl
import com.popalay.barnee.util.videoId
import io.matthewnelson.component.parcelize.Parcelize
import kotlinx.parcelize.IgnoredOnParcel
import org.koin.core.parameter.parametersOf
import kotlin.math.min
import kotlin.math.roundToInt

@Parcelize
data class DrinkScreen(override val input: DrinkMinimumData) : ScreenWithInputAsKey<DrinkMinimumData> {

    @IgnoredOnParcel
    override val transition: ScreenWithTransition.Transition = ScreenWithTransition.Transition.SlideVertical

    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<DrinkStateMachine>(parameters = { parametersOf(input) })
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()
        val drinkItemStateMachine = injectStateMachine<DrinkItemStateMachine>()

        DrinkScreen(state, stateMachine::dispatch, drinkItemStateMachine::dispatch)
    }
}

@Composable
private fun DrinkScreen(
    state: DrinkState,
    onAction: (Action) -> Unit,
    onItemAction: (DrinkItemAction) -> Unit
) {
    val screenWidthDp = with(LocalConfiguration.current) { remember(this) { screenWidthDp.dp } }
    val toolbarHeightPx = with(LocalDensity.current) { (screenWidthDp / DefaultAspectRatio).toPx() }
    val collapsedToolbarHeightPx = with(LocalDensity.current) { ActionsAppBarHeight.toPx() + LocalWindowInsets.current.statusBars.top }
    val activity = findActivity()
    val keepScreenOnFlag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    val listState = rememberLazyListState()
    val collapsingScaffoldState = rememberCollapsingScaffoldState(
        minHeight = collapsedToolbarHeightPx,
        maxHeight = toolbarHeightPx
    )

    DisposableEffect(Unit) {
        onDispose {
            activity?.window?.clearFlags(keepScreenOnFlag)
        }
    }

    CollapsingScaffold(
        state = collapsingScaffoldState,
        isEnabled = state.drinkWithRelated is Success,
        topBar = {
            DrinkAppBar(
                state = state,
                onAction = onAction,
                onDrinkAction = onItemAction,
                scrollFraction = collapsingScaffoldState.fraction,
                offset = IntOffset(0, collapsingScaffoldState.topBarOffset.roundToInt()),
                modifier = Modifier.offset { IntOffset(0, collapsingScaffoldState.topBarOffset.roundToInt()) }
            )
        }
    ) { contentPadding ->
        DrinkScreenBody(
            drinkWithRelated = state.drinkWithRelated,
            listState = listState,
            contentPadding = contentPadding,
            onRetryClicked = { onAction(DrinkAction.Retry) },
            onCategoryClicked = { onAction(NavigateToAction(AppScreens.DrinksByTag(it.text))) },
            onMoreRecommendedDrinksClicked = { onAction(NavigateToAction(AppScreens.SimilarDrinksTo(state.drinkMinimumData))) }
        )
    }
}

@Composable
private fun DrinkScreenBody(
    drinkWithRelated: Result<FullDrinkResponse>,
    listState: LazyListState,
    onRetryClicked: () -> Unit,
    onCategoryClicked: (Category) -> Unit,
    onMoreRecommendedDrinksClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        item {
            StateLayout(
                value = drinkWithRelated,
                loadingState = {
                    LoadingStateView(
                        modifier = Modifier.padding(top = 32.dp)
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
                            modifier = Modifier.padding(start = 24.dp, end = 16.dp)
                        )
                        Divider(modifier = Modifier.padding(vertical = 24.dp))
                    }
                    Ingredients(
                        ingredient = value.drink.ingredients,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 24.dp))
                    Steps(
                        instruction = value.drink.instruction,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    if (value.drink.keywords.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(vertical = 24.dp))
                        Keywords(
                            keywords = value.drink.keywords,
                            onClick = { onCategoryClicked(it) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    if (value.relatedDrinks.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(vertical = 24.dp))
                        RecommendedDrinks(
                            data = value.relatedDrinks,
                            onShowMoreClick = onMoreRecommendedDrinksClicked,
                        )
                    }
                    Spacer(modifier = Modifier.navigationBarsHeight(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DrinkAppBar(
    state: DrinkState,
    onAction: (Action) -> Unit,
    onDrinkAction: (DrinkItemAction) -> Unit,
    scrollFraction: Float,
    offset: IntOffset,
    modifier: Modifier = Modifier,
) {
    val drink by remember(state.drinkWithRelated) { derivedStateOf { state.drinkWithRelated()?.drink } }
    val hasVideo by remember(drink) { derivedStateOf { !drink?.videoId.isNullOrBlank() } }
    val titleMaxLines by remember(hasVideo) { derivedStateOf { if (hasVideo) 3 else 6 } }
    val contentAlpha by remember(state, scrollFraction) {
        derivedStateOf { if (state.isPlaying) 0F else 1 - (scrollFraction * 1.5F).coerceAtMost(1F) }
    }
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
            .then(if (isLandscape) Modifier.fillMaxHeight() else Modifier.aspectRatio(DefaultAspectRatio))
    ) {
        Crossfade(state.isPlaying, label = "player-content") { isPlaying ->
            if (isPlaying) {
                YouTubePlayer(
                    videoId = drink?.videoId.orEmpty(),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(bottom = 16.dp)
                )
            } else {
                AsyncImage(
                    imageUrl = state.displayImage,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = ContentAlpha.disabled), BlendMode.SrcAtop)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(scrollFraction)
                .background(MaterialTheme.colors.background)
        )

        ConstraintLayout {
            val (actionBar, title, nutrition) = createRefs()
            val (collection, bottomSection, playButton) = createRefs()
            CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
                Text(
                    text = state.displayName,
                    style = MaterialTheme.typography.h1,
                    maxLines = titleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start, 24.dp)
                        top.linkTo(actionBar.bottom)
                        width = Dimension.percent(0.7F)
                    }
                )
                Text(
                    text = drink?.calories.orEmpty(),
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.constrainAs(nutrition) {
                        start.linkTo(parent.start, 24.dp)
                        top.linkTo(title.bottom, 8.dp)
                    }
                )
                CollectionBanner(
                    collection = state.drinkWithRelated()?.drink?.collection,
                    onCollectionClick = { onAction(NavigateToAction(AppScreens.SingleCollection(it))) },
                    modifier = Modifier.constrainAs(collection) {
                        start.linkTo(nutrition.end, 24.dp)
                        centerVerticallyTo(nutrition)
                    }
                )
                AnimatedVisibility(
                    visible = drink != null,
                    enter = slideInVertically { it },
                    modifier = Modifier.constrainAs(bottomSection) {
                        start.linkTo(parent.start, 24.dp)
                        end.linkTo(parent.end, 8.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (drink?.isGenerated == true) "AI ✨" else drink?.displayRatingWithMax.orEmpty(),
                            style = MaterialTheme.typography.h2,
                        )

                        if (drink?.isGenerated == false) {
                            AnimatedHeartButton(
                                onToggle = { onDrinkAction(DrinkItemAction.ToggleFavorite(requireNotNull(drink))) },
                                isSelected = drink?.collection != null,
                                iconSize = 32.dp,
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !drink?.videoId.isNullOrBlank(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.constrainAs(playButton) {
                        top.linkTo(nutrition.bottom)
                        bottom.linkTo(bottomSection.top)
                        centerHorizontallyTo(parent)
                    }
                ) {
                    PlayButton(onClick = { onAction(DrinkAction.TogglePlaying) })
                }
            }
            DrinkActionBar(
                title = state.displayName,
                titleAlpha = 1 - contentAlpha,
                isActionsVisible = drink != null,
                isScreenKeptOn = state.isScreenKeptOn,
                onBackCLick = { onAction(NavigateBackAction) },
                onKeepScreenOnClicked = { onAction(DrinkAction.KeepScreenOnClicked) },
                onShareClicked = { onAction(DrinkAction.ShareClicked) },
                modifier = Modifier
                    .constrainAs(actionBar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .offset { -offset }
            )
        }
    }
}

@Composable
private fun DrinkActionBar(
    title: String,
    titleAlpha: Float,
    isActionsVisible: Boolean,
    isScreenKeptOn: Boolean,
    onKeepScreenOnClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onBackCLick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ActionsAppBar(
        title = {
            Text(
                text = title,
                color = LocalContentColor.current.copy(alpha = titleAlpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingButtons = { BackButton(onClick = onBackCLick, iconRes = R.drawable.ic_cross) },
        trailingButtons = {
            AnimatedVisibility(isActionsVisible) {
                Row {
                    IconButton(onClick = onKeepScreenOnClicked) {
                        Icon(
                            painter = if (isScreenKeptOn) painterResource(R.drawable.ic_light_off) else painterResource(R.drawable.ic_light_on),
                            contentDescription = "Keep screen on",
                        )
                    }
                    IconButton(onClick = onShareClicked) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share drink",
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun CollectionBanner(
    collection: Collection?,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
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
                .clickable { onCollectionClick(requireNotNull(collection)) }
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

@Composable
private fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = LocalContentColor.current.copy(alpha = min(ContentAlpha.disabled, LocalContentAlpha.current)),
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
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

    Column(
        modifier = modifier.clickable(
            onClick = { isTextCollapsed = !isTextCollapsed },
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    ) {
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
                    modifier = Modifier
                        .size(8.dp)
                        .rotate(arrowRotation.toFloat())
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = story.take(textLength) + if (isTextCollapsed) "..." else "",
            style = MaterialTheme.typography.body2,
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
            modifier = Modifier.padding(start = 24.dp, end = 16.dp)
        ) {
            Text(
                text = "Recommended",
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F)
            )
            TextButton(
                onClick = onShowMoreClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onBackground),
            ) {
                Text(
                    text = "More",
                    style = MaterialTheme.typography.subtitle2,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "More",
                    modifier = Modifier
                        .size(8.dp)
                        .rotate(180F)
                )
            }
        }
        DrinkHorizontalList(
            data = data,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DrinkScreenPreview() {
    BarneeTheme {
        DrinkScreen(DrinkMinimumData("alias", "name", "sample.png".toImageUrl()))
    }
}
