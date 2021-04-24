package com.popalay.barnee.di

import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.ui.screen.discovery.DiscoveryViewModel
import com.popalay.barnee.ui.screen.drink.DrinkViewModel
import com.popalay.barnee.ui.screen.drinklist.DrinkListViewModel
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListViewModel
import com.popalay.barnee.ui.screen.search.SearchViewModel
import com.popalay.barnee.ui.screen.shaketodrink.ShakeToDrinkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { DiscoveryViewModel(get()) }
    viewModel { (alias: String) -> DrinkViewModel(get { parametersOf(alias) }) }
    viewModel { SearchViewModel(get()) }
    viewModel { (request: DrinksRequest) -> ParameterizedDrinkListViewModel(get { parametersOf(request) }) }
    viewModel { DrinkListViewModel(get()) }
    viewModel { ShakeToDrinkViewModel(get()) }
}