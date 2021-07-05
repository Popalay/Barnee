package com.popalay.barnee.di

import com.popalay.barnee.ui.screen.addtocollection.AddToCollectionViewModel
import com.popalay.barnee.ui.screen.collection.CollectionViewModel
import com.popalay.barnee.ui.screen.collectionlist.CollectionListViewModel
import com.popalay.barnee.ui.screen.discovery.DiscoveryViewModel
import com.popalay.barnee.ui.screen.drink.DrinkViewModel
import com.popalay.barnee.ui.screen.drinklist.DrinkItemViewModel
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListViewModel
import com.popalay.barnee.ui.screen.search.SearchViewModel
import com.popalay.barnee.ui.screen.shaketodrink.ShakeToDrinkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { DiscoveryViewModel(get { it }) }
    viewModel { DrinkViewModel(get { it }) }
    viewModel { SearchViewModel(get { it }) }
    viewModel { ParameterizedDrinkListViewModel(get { it }) }
    viewModel { DrinkItemViewModel(get { it }) }
    viewModel { ShakeToDrinkViewModel(get { it }) }
    viewModel { CollectionViewModel(get { it }) }
    viewModel { CollectionListViewModel(get { it }) }
    viewModel { AddToCollectionViewModel(get { it }) }
}
