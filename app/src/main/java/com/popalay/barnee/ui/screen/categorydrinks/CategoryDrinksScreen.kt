package com.popalay.barnee.ui.screen.categorydrinks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
fun CategoryDrinksScreen(
    tag: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController: NavController = LocalNavController.current
    val viewModel: CategoryDrinksViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    LaunchedEffect(tag) {
        viewModel.loadDrinks(tag)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = buildAnnotatedString {
                append("Cocktails with ")
                withStyle(SpanStyle(color = MaterialTheme.colors.primary)) {
                    append(tag)
                }
            },
            style = MaterialTheme.typography.h1,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        )
        DrinkList(
            navController = navController,
            drinks = state.drinks,
            emptyMessage = "We don't have any drinks for this category",
            contentPadding = contentPadding
        )
    }

}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun CategoryDrinksScreenLightPreview() {
    BarneeTheme {
        CategoryDrinksScreen("")
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun CategoryDrinksScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        CategoryDrinksScreen("")
    }
}