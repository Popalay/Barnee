@file:OptIn(ExperimentalSettingsImplementation::class)
package com.popalay.barnee.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.popalay.barnee.data.remote.HtmlExtractor
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore("Settings")

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule = module {
    single<FlowSettings> { DataStoreSettings(get<Context>().dataStore) }
}