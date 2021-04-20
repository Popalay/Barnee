package com.popalay.barnee.ui.screen.parameterizeddrinklist

import androidx.compose.runtime.Composable
import com.popalay.barnee.data.repository.DrinksRequest
import java.util.Locale

@Composable
fun SimilarDrinksScreen(
    alias: String,
    name: String
) {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.RelatedTo(alias),
        title = "Cocktails like ",
        titleHighlighted = name
    )
}

@Composable
fun FavoriteDrinksScreen() {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.Favorites,
        title = "Favorites",
    )
}

@Composable
fun TagDrinksScreen(tag: String) {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.ForTags(setOf(tag)),
        title = tag.capitalize(Locale.getDefault())
    )
}

@Composable
fun QueryDrinksScreen(
    query: String,
    name: String
) {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.ForQuery(query),
        title = name.capitalize(Locale.getDefault())
    )
}