package com.popalay.barnee.di

import com.popalay.barnee.data.repository.DrinkRepositoryNative
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule = module {
    single { DrinkRepositoryNative(get()) }
    single {
        val delegate = NSUserDefaults("Settings")
        AppleSettings(delegate).toFlowSettings()
    }
}

class DiComponent : KoinComponent {
    fun provideDrinkRepository(): DrinkRepositoryNative = get()
}