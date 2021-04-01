package com.popalay.barnee.di

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.remote.HtmlExtractor
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.domain.categorydrinks.CategoryDrinksStateMachine
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinklist.DrinkListStateMachine
import com.popalay.barnee.domain.favorites.FavoritesStateMachine
import com.popalay.barnee.domain.receipt.ReceiptStateMachine
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.domain.similardrinks.SimilarDrinksStateMachine
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

    factory { DiscoveryStateMachine(get()) }
    factory { CategoryDrinksStateMachine(get()) }
    factory { DrinkStateMachine(get()) }
    factory { FavoritesStateMachine(get()) }
    factory { ReceiptStateMachine() }
    factory { SearchStateMachine(get()) }
    factory { SimilarDrinksStateMachine(get()) }
    factory { DrinkListStateMachine(get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule, platformModule)
}

fun initKoin() = startKoin {
    modules(commonModule, platformModule)
}