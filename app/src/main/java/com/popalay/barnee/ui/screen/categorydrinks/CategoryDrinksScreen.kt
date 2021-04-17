package com.popalay.barnee.ui.screen.categorydrinks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoryDrinksScreen(tag: String) {
    val viewModel: CategoryDrinksViewModel = getViewModel { parametersOf(tag) }
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        ActionsAppBar(
            title = buildAnnotatedString {
                append("Cocktails category: ")
                withStyle(SpanStyle(color = MaterialTheme.colors.primary)) {
                    append(tag)
                }
            },
            modifier = Modifier.liftOnScroll(listState),
            leadingButtons = { BackButton() }
        )
        DrinkGrid(
            drinks = state.drinks,
            listState = listState,
            emptyMessage = "We don't have any drinks\nfor this category",
            contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues()
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CategoryDrinksScreenPreview() {
    BarneeTheme {
        CategoryDrinksScreen("")
    }
}