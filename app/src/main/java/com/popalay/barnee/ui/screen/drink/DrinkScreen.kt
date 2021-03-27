package com.popalay.barnee.ui.screen.drink

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.popalay.barnee.data.model.InstructionStep
import com.popalay.barnee.data.model.Nutrition
import com.popalay.barnee.domain.drink.DrinkAction
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.screen.navigation.Screen
import com.popalay.barnee.ui.theme.BarneeTheme
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsPadding
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

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            StartMixingButton(
                onClick = {
                    state.receipt()?.let {
                        navController.navigate(
                            Screen.Receipt(
                                it.recipeInstructions.map(InstructionStep::text),
                                image,
                                it.videoUrl
                            ).route
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .navigationBarsPadding()
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ImageHeader(
                name = name,
                image = image,
                isFavorite = state.receipt()?.isFavorite ?: false,
                onClickLike = { viewModel.consumeAction(DrinkAction.ToggleFavorite(alias)) }
            )
            StateLayout(
                value = state.receipt,
                loadingState = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            ) { value ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    Ingredients(recipeIngredient = value.recipeIngredient)
                    Nutrition(nutrition = value.nutrition)
                }
                Keywords(
                    keywords = value.keywordsArray,
                    onClick = { navController.navigate(Screen.CategoryDrinks(it).route) },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
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
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun StartMixingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        text = { Text(text = "Start mixing") },
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun ImageHeader(
    name: String,
    image: String,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    onClickLike: () -> Unit
) {
    Box(modifier = modifier) {
        CoilImage(
            data = image,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1F)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8F)),
                        startY = 10F,
                    )
                )
                .align(Alignment.BottomStart)
                .padding(32.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h1,
                modifier = Modifier.weight(1F)
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = onClickLike,
                modifier = Modifier.align(Alignment.Bottom)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Like",
                    tint = if (isFavorite) MaterialTheme.colors.secondary else Color.White.copy(alpha = 0.5F),
                    modifier = Modifier.size(72.dp)
                )
            }
        }
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
            style = MaterialTheme.typography.h2,
        )
        Text(
            text = recipeIngredient.joinToString("\n"),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun Nutrition(
    nutrition: Nutrition,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Nutrition",
            style = MaterialTheme.typography.h2,
        )
        Text(
            text = nutrition.calories,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp)
        )
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
            style = MaterialTheme.typography.h2,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        FlowRow {
            keywords.forEach { item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
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