package com.popalay.barnee.di

import com.popalay.barnee.data.remote.ApiNative
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.dsl.module

actual val platformModule = module {
    single { ApiNative(get()) }
}

class DiComponent : KoinComponent {
    fun provideApi(): ApiNative = get()
}