package com.popalay.barnee.ui.screen.similardrinks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.toPaddingValues
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SimilarDrinksScreen(
    alias: String,
    name: String
) {
    val viewModel: SimilarDrinksViewModel = getViewModel { parametersOf(alias) }
    val state by viewModel.stateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = buildAnnotatedString {
                append("Cocktails like ")
                withStyle(SpanStyle(color = MaterialTheme.colors.primary)) {
                    append(name)
                }
            },
            style = MaterialTheme.typography.h1,
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        )
        DrinkGrid(
            drinks = state.drinks,
            emptyMessage = "We don't have any drinks\nfor this category",
            contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues()
        )
    }

}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun SimilarDrinksScreenLightPreview() {
    BarneeTheme {
        SimilarDrinksScreen("alias", "name")
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun SimilarDrinksScreenDarkPreview() {
    BarneeTheme(darkTheme = true) {
        SimilarDrinksScreen("alias", "name")
    }
}