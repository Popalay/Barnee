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
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.popalay.barnee.R
import com.popalay.barnee.R.string
import com.popalay.barnee.domain.discovery.DiscoveryState
import com.popalay.barnee.navigation.AppNavigation
import com.popalay.barnee.navigation.LocalNavController
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun DiscoveryScreen() {
    val viewModel: DiscoveryViewModel = getViewModel()
    DiscoveryScreen(viewModel)
}

@Composable
private fun DiscoveryScreen(viewModel: DiscoveryViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    DiscoveryScreen(state)
}

@Composable
private fun DiscoveryScreen(state: DiscoveryState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        DiscoveryAppBar(modifier = Modifier.liftOnScroll(listState))
        CategoryGrid(
            categories = state.categories,
            emptyMessage = "We currently have no drinks",
            listState = listState,
            contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues(
                additionalHorizontal = 8.dp
            )
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
            IconButton(onClick = { navController.navigate(AppNavigation.collections()) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_collections),
                    contentDescription = "Favorites",
                )
            }
            IconButton(onClick = { navController.navigate(AppNavigation.search()) }) {
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