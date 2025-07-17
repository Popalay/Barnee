/*
 * Copyright (c) 2025 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.ui.navigation

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.collection.CollectionInput
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.ui.screen.addtocollection.AddToCollectionScreen
import com.popalay.barnee.ui.screen.bartender.BartenderScreen
import com.popalay.barnee.ui.screen.collection.CollectionScreen
import com.popalay.barnee.ui.screen.collectionlist.CollectionListScreen
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.drink.DrinkScreen
import com.popalay.barnee.ui.screen.generateddrinks.GeneratedDrinksScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListScreen
import com.popalay.barnee.ui.screen.search.SearchScreen
import com.popalay.barnee.util.capitalizeFirstChar

private val mainScreenModule = screenModule {
    register<AppScreens.Discovery> {
        DiscoveryScreen()
    }
    register<AppScreens.SingleCategory> { provider ->
        ParameterizedDrinkListScreen(
            ParameterizedDrinkListInput(
                request = DrinksRequest.ForQuery(provider.category.alias),
                emptyStateMessage = "No drinks found for \"${provider.category.alias}\"",
                title = provider.category.text.capitalizeFirstChar()
            )
        )
    }
    register<AppScreens.DrinksByTag> { provider ->
        ParameterizedDrinkListScreen(
            ParameterizedDrinkListInput(
                request = DrinksRequest.ForTags(setOf(provider.tag)),
                emptyStateMessage = "No drinks found for \"${provider.tag}\"",
                title = provider.tag,
            )
        )
    }
    register<AppScreens.GeneratedDrinks> {
        GeneratedDrinksScreen(
            ParameterizedDrinkListInput(
                request = DrinksRequest.Generated,
                emptyStateMessage = "No have no generated drinks yet.\nLet's make some! 🍸",
                title = "Your Bartender",
            )
        )
    }
    register<AppScreens.SimilarDrinksTo> { provider->
        ParameterizedDrinkListScreen(
            ParameterizedDrinkListInput(
                request = DrinksRequest.RelatedTo(provider.drink.identifier),
                emptyStateMessage = "No drinks found for \"${provider.drink.name}\"",
                title = "Similar to ",
                titleHighlighted = provider.drink.name
            )
        )
    }
    register<AppScreens.SingleCollection> { provider ->
        CollectionScreen(
            CollectionInput(
                name = provider.collection.name,
                aliases = provider.collection.aliases
            )
        )
    }
    register<AppScreens.Drink> { provider ->
        DrinkScreen(provider.drink)
    }
    register<AppScreens.Collections> {
        CollectionListScreen()
    }
    register<AppScreens.Bartender> {
        BartenderScreen()
    }
    register<AppScreens.Search> {
        SearchScreen()
    }
    register<AppScreens.AddToCollection> { provider ->
        AddToCollectionScreen(provider.drink)
    }
}

fun registerScreens() {
    ScreenRegistry {
        mainScreenModule()
    }
}
