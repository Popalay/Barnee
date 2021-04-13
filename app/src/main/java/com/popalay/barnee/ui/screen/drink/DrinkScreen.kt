package com.popalay.barnee.ui.screen.drink

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.popalay.barnee.R.drawable
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.FullDrink
import com.popalay.barnee.data.model.Ingredient
import com.popalay.barnee.data.model.Instruction
import com.popalay.barnee.domain.Success
import com.popalay.barnee.domain.drink.DrinkAction
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.CollapsingScaffold
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.YouTubePlayer
import com.popalay.barnee.ui.screen.drinklist.DrinkHorizontalList
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.screen.navigation.Screen
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.theme.LightGrey
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DrinkScreen(
    alias: String,
    image: String,
    name: String,
) {
    val navController: NavController = LocalNavController.current
    val viewModel: DrinkViewModel = getViewModel { parametersOf(alias) }
    val state by viewModel.stateFlow.collectAsState()

    val configuration = LocalConfiguration.current
    val toolbarHeight = remember { Dp(configuration.screenWidthDp / 0.8F) }
    val collapsedToolbarHeight = 96.dp + with(LocalDensity.current) { LocalWindowInsets.current.statusBars.bottom.toDp() }

    CollapsingScaffold(
        maxHeight = toolbarHeight,
        minHeight = collapsedToolbarHeight,
        isEnabled = state.drinkWithRelated is Success,
        appBarContent = { fraction, offset ->
            DrinkAppBar(
                title = name,
                image = image,
                data = state.drinkWithRelated()?.drink,
                isPlaying = state.isPlaying,
                offset = offset,
                scrollFraction = fraction,
                onClickLike = { viewModel.processAction(DrinkAction.ToggleFavorite(alias)) },
                onClickPlay = { viewModel.processAction(DrinkAction.TogglePlaying) },
                modifier = Modifier.offset { offset }
            )
        }
    ) { contentPadding ->
        LazyColumn(contentPadding = contentPadding) {
            item {
                StateLayout(
                    value = state.drinkWithRelated,
                    loadingState = {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .offset(y = (-8).dp)
                        )
                    },
                ) { value ->
                    Spacer(modifier = Modifier.height(32.dp))
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
                    Keywords(
                        keywords = value.drink.keywords,
                        onClick = { navController.navigate(Screen.CategoryDrinks(it).route) },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 24.dp))
                    RecommendedDrinks(
                        data = value.relatedDrinks,
                        onShowMoreClick = { navController.navigate(Screen.SimilarDrinks(alias, value.drink.name).route) }
                    )
                    Spacer(modifier = Modifier.navigationBarsHeight(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun DrinkAppBar(
    title: String,
    image: String,
    data: FullDrink?,
    isPlaying: Boolean,
    offset: IntOffset,
    scrollFraction: Float,
    modifier: Modifier = Modifier,
    onClickLike: () -> Unit,
    onClickPlay: () -> Unit
) {
    Card(
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium.copy(topStart = CornerSize(0), topEnd = CornerSize(0)),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8F)
    ) {
        val titleTextSize = remember(scrollFraction) { (56 * (1 - scrollFraction)).coerceAtLeast(24F) }

        Box {
            if (isPlaying) {
                YouTubePlayer(
                    uri = data?.videoUrl.orEmpty(),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(bottom = 16.dp)
                )
            } else {
                CoilImage(
                    data = image,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.3F), BlendMode.SrcAtop),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(scrollFraction)
                    .background(MaterialTheme.colors.background)
            )
            if (!isPlaying) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 32.dp, end = 88.dp, top = 88.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h1.copy(fontSize = titleTextSize.sp),
                        modifier = Modifier
                            .offset { -offset }
                            .offset(
                                x = 32.dp * scrollFraction,
                                y = (-50).dp * scrollFraction
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data?.nutrition?.totalCalories?.toString()?.let { "$it kcal" }.orEmpty(),
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.alpha(1 - scrollFraction)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4F)
                        .align(Alignment.BottomCenter)
                        .alpha(1 - scrollFraction)
                ) {
                    IconButton(
                        onClick = onClickLike,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 24.dp, bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (data?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = data?.displayRating.orEmpty(),
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 32.dp, bottom = 16.dp)
                    )
                    if (!data?.videoUrl.isNullOrBlank()) {
                        IconButton(
                            onClick = onClickPlay,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .align(Alignment.TopCenter)
                                .background(LocalContentColor.current.copy(alpha = ContentAlpha.disabled))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            BackButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 32.dp, start = 16.dp)
                    .offset { -offset }
            )
        }
    }
}

@Composable
fun Ingredients(
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
fun Steps(
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
                    text = item.displayText,
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
fun Keywords(
    keywords: List<String>,
    onClick: (String) -> Unit,
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
                    text = item,
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
fun RecommendedDrinks(
    data: List<Drink>,
    onShowMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 32.dp, end = 16.dp)
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
                    painter = painterResource(drawable.ic_arrow_back),
                    contentDescription = "More",
                    modifier = Modifier.size(16.dp).rotate(180F)
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
@Composable
fun DrinkScreenLightPreview() {
    BarneeTheme {
        DrinkScreen("alias", "name", "sample.png")
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DrinkScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        DrinkScreen("alias", "name", "sample.png")
    }
}