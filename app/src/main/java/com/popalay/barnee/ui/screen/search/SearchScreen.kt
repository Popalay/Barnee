package com.popalay.barnee.ui.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
fun SearchScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController: NavController = LocalNavController.current
    val viewModel: SearchViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    Column(modifier = modifier.statusBarsPadding()) {
        OutlinedTextField(
            value = state.searchQuery,
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        DrinkList(
            navController = navController,
            drinks = state.drinks,
            emptyMessage = "We currently have no drinks on your request",
            contentPadding = contentPadding
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun SearchScreenLightPreview() {
    BarneeTheme {
        SearchScreen()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun SearchScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        SearchScreen()
    }
}