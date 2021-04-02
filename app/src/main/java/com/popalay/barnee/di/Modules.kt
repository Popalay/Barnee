package com.popalay.barnee.di

import com.popalay.barnee.ui.screen.categorydrinks.CategoryDrinksViewModel
import com.popalay.barnee.ui.screen.discovery.DiscoveryViewModel
import com.popalay.barnee.ui.screen.drink.DrinkViewModel
import com.popalay.barnee.ui.screen.drinklist.DrinkListViewModel
import com.popalay.barnee.ui.screen.favorites.FavoritesViewModel
import com.popalay.barnee.ui.screen.search.SearchViewModel
import com.popalay.barnee.ui.screen.similardrinks.SimilarDrinksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { DiscoveryViewModel(get()) }
    viewModel { (tag: String) -> CategoryDrinksViewModel(tag, get()) }
    viewModel { (alias: String) -> DrinkViewModel(alias, get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { (alias: String) -> SimilarDrinksViewModel(alias, get()) }
    viewModel { DrinkListViewModel(get()) }
}