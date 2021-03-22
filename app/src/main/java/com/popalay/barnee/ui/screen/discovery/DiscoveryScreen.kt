package com.popalay.barnee.ui.screen.discovery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.popalay.barnee.ui.common.DrinkList
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.theme.BarneeTheme
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun DiscoveryScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController: NavController = LocalNavController.current
    val viewModel: DiscoveryViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    Column(modifier = modifier.statusBarsPadding()) {
        Text(
            text = "Discover new",
            style = MaterialTheme.typography.h1,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
        )
        DrinkList(
            navController = navController,
            drinks = state.drinks,
            emptyMessage = "We currently have no drinks",
            contentPadding = contentPadding
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun DiscoveryScreenLightPreview() {
    BarneeTheme {
        DiscoveryScreen()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DiscoveryScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        DiscoveryScreen()
    }
}