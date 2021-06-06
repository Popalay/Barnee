package com.popalay.barnee.ui.screen.parameterizeddrinklist

import androidx.compose.runtime.Composable
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.navigation.QueryDrinksScreenArgs
import com.popalay.barnee.navigation.SimilarDrinksScreenArgs
import com.popalay.barnee.navigation.TagDrinksScreenArgs
import com.popalay.barnee.ui.util.capitalizeFirstChar

@Composable
fun SimilarDrinksScreen(args: SimilarDrinksScreenArgs) {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.RelatedTo(args.alias),
        title = "Cocktails like ",
        titleHighlighted = args.name
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
fun TagDrinksScreen(args: TagDrinksScreenArgs) {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.ForTags(setOf(args.tag)),
        title = args.tag.capitalizeFirstChar()
    )
}

@Composable
fun QueryDrinksScreen(args: QueryDrinksScreenArgs) {
    ParameterizedDrinkListScreen(
        request = DrinksRequest.ForQuery(args.query),
        title = args.name.capitalizeFirstChar()
    )
}