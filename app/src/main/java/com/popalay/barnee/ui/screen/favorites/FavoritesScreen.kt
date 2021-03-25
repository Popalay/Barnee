package com.popalay.barnee.ui.screen.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.popalay.barnee.ui.common.DrinkList
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.theme.BarneeTheme
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import org.koin.androidx.compose.getViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController: NavController = LocalNavController.current
    val viewModel: FavoritesViewModel = getViewModel()
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = modifier.statusBarsPadding()) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        )
        DrinkList(
            navController = navController,
            drinks = state.drinks,
            emptyMessage = "You don't have favorites drinks yet",
            contentPadding = contentPadding
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun FavoritesScreenLightPreview() {
    BarneeTheme {
        FavoritesScreen()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun FavoritesScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        FavoritesScreen()
    }
}