package com.popalay.barnee.ui.screen.drink

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsHeight
import com.popalay.barnee.data.model.InstructionStep
import com.popalay.barnee.data.model.Receipt
import com.popalay.barnee.domain.drink.DrinkAction
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.YouTubePlayer
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

    Scaffold {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ImageHeader(
                name = name,
                image = image,
                data = state.receipt(),
                isPlaying = state.isPlaying,
                onBackClick = { navController.popBackStack() },
                onClickLike = { viewModel.processAction(DrinkAction.ToggleFavorite(alias)) },
                onClickPlay = { viewModel.processAction(DrinkAction.TogglePlaying) },
                modifier = Modifier.clip(MaterialTheme.shapes.medium.copy(topStart = CornerSize(0), topEnd = CornerSize(0)))
            )
            StateLayout(
                value = state.receipt,
                loadingState = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            ) { value ->
                Spacer(modifier = Modifier.height(56.dp))
                Ingredients(
                    recipeIngredient = value.recipeIngredient,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 32.dp))
                Steps(
                    steps = value.recipeInstructions,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 32.dp))
                Keywords(
                    keywords = value.keywordsArray,
                    onClick = { navController.navigate(Screen.CategoryDrinks(it).route) },
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 32.dp))
                Text(
                    text = "Similar cocktails",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Screen.SimilarDrinks(alias, value.name).route) }
                        .padding(vertical = 4.dp)
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.navigationBarsHeight())
            }
        }
    }
}

@Composable
private fun ImageHeader(
    name: String,
    image: String,
    data: Receipt?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onClickLike: () -> Unit,
    onClickPlay: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8F)
    ) {
        if (isPlaying) {
            YouTubePlayer(
                uri = data?.videoUrl.orEmpty(),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        } else {
            CoilImage(
                data = image,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 32.dp, end = 88.dp, top = 88.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h1,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data?.nutrition?.calories.orEmpty(),
                    style = MaterialTheme.typography.h3,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4F)
                    .align(Alignment.BottomCenter)
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
                    text = "10.0",
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
                            tint = Color.White,
                            contentDescription = "Play",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        BackButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp, start = 16.dp)
        )
    }
}

@Composable
fun Ingredients(
    recipeIngredient: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.height(24.dp))
        recipeIngredient.forEachIndexed { index, item ->
            if (index > 0) Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item,
                style = MaterialTheme.typography.body1,
                color = LightGrey
            )
        }
    }
}

@Composable
fun Steps(
    steps: List<InstructionStep>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Steps",
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier.height(24.dp))
        steps.forEachIndexed { index, item ->
            if (index > 0) Spacer(modifier = Modifier.height(16.dp))
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
                    style = MaterialTheme.typography.body1,
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