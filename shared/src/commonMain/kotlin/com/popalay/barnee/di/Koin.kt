package com.popalay.barnee.di

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.remote.HtmlExtractor
import com.popalay.barnee.data.repository.DrinkRepository
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    single { Api(get(), get()) }
    single { LocalStore(get()) }
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single { DrinkRepository(get(), get()) }
    single { HtmlExtractor() }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule, platformModule)
}

fun initKoin() = startKoin {
    modules(commonModule, platformModule)
}