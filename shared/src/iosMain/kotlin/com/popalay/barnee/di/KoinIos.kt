package com.popalay.barnee.di

import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkStateMachine
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class)
actual val platformModule = module {
    single {
        val delegate = NSUserDefaults("Settings")
        AppleSettings(delegate).toFlowSettings()
    }
}

class DiComponent : KoinComponent {
    fun provideDiscoveryStateMachine(): DiscoveryStateMachine = get()
    fun provideDrinkStateMachine(): DrinkStateMachine = get()
    fun provideReceiptStateMachine(): DrinkStateMachine = get()
    fun provideSearchStateMachine(): SearchStateMachine = get()
    fun provideSimilarDrinksStateMachine(): ParameterizedDrinkListStateMachine = get()
    fun provideDrinkListStateMachine(): DrinkItemStateMachine = get()
    fun provideShakeToDrinkStateMachine(): ShakeToDrinkStateMachine = get()
}