@file:OptIn(ExperimentalCoroutinesApi::class)

package com.popalay.barnee.di

import com.popalay.barnee.domain.categorydrinks.CategoryDrinksStateMachine
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinklist.DrinkListStateMachine
import com.popalay.barnee.domain.favorites.FavoritesStateMachine
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.domain.similardrinks.SimilarDrinksStateMachine
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule = module {
    single {
        val delegate = NSUserDefaults("Settings")
        AppleSettings(delegate).toFlowSettings()
    }
}

class DiComponent : KoinComponent {
    fun provideDiscoveryStateMachine(): DiscoveryStateMachine = get()
    fun provideCategoryDrinksStateMachine(): CategoryDrinksStateMachine = get()
    fun provideDrinkStateMachine(): DrinkStateMachine = get()
    fun provideFavoritesStateMachine(): FavoritesStateMachine = get()
    fun provideReceiptStateMachine(): DrinkStateMachine = get()
    fun provideSearchStateMachine(): SearchStateMachine = get()
    fun provideSimilarDrinksStateMachine(): SimilarDrinksStateMachine = get()
    fun provideDrinkListStateMachine(): DrinkListStateMachine = get()
}