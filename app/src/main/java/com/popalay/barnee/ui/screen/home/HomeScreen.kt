package com.popalay.barnee.ui.screen.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition.Center
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.toPaddingValues
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.favorites.FavoritesScreen
import com.popalay.barnee.ui.screen.navigation.TabScreen
import com.popalay.barnee.ui.screen.navigation.homeScreens
import com.popalay.barnee.ui.screen.search.SearchScreen
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.theme.backgroundVariant

@Composable
fun HomeScreen() {
    val localNavController = rememberNavController()
    val insets = LocalWindowInsets.current

    Scaffold(
        floatingActionButtonPosition = Center,
        floatingActionButton = {
            val navBackStackEntry by localNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.backgroundVariant.copy(alpha = 0.93F),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(40))
                    .height(48.dp)
            ) {
                homeScreens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, screen.title) },
                        selected = currentRoute == screen.route,
                        selectedContentColor = MaterialTheme.colors.primary,
                        onClick = {
                            localNavController.navigate(screen.route) {
                                popUpTo = localNavController.graph.startDestination
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(localNavController, startDestination = TabScreen.Discovery.route) {
            composable(TabScreen.Discovery.route) {
                DiscoveryScreen(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = insets.navigationBars.toPaddingValues(additionalBottom = 72.dp)
                )
            }
            composable(TabScreen.Search.route) {
                SearchScreen(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = insets.navigationBars.toPaddingValues(additionalBottom = 72.dp)
                )
            }
            composable(TabScreen.Favorites.route) {
                FavoritesScreen(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = insets.navigationBars.toPaddingValues(additionalBottom = 72.dp)
                )
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenLightPreview() {
    BarneeTheme {
        HomeScreen()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        HomeScreen()
    }
}