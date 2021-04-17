package com.popalay.barnee.ui.screen.discovery

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.navigate
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.popalay.barnee.R
import com.popalay.barnee.R.string
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.screen.navigation.Screen
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun DiscoveryScreen() {
    val viewModel: DiscoveryViewModel = getViewModel()
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        DiscoveryAppBar(modifier = Modifier.liftOnScroll(listState))
        DrinkGrid(
            drinks = state.drinks,
            emptyMessage = "We currently have no drinks",
            listState = listState,
            contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues()
        )
    }
}

@Composable
private fun DiscoveryAppBar(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    ActionsAppBar(
        title = stringResource(string.app_name),
        modifier = modifier,
        trailingButtons = {
            IconButton(onClick = { navController.navigate(Screen.Favorites.route) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorites),
                    contentDescription = "Favorites",
                )
            }
            IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                )
            }
        }
    )
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DiscoveryScreenPreview() {
    BarneeTheme {
        DiscoveryScreen()
    }
}