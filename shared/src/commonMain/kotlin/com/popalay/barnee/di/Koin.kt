package com.popalay.barnee.di

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkStateMachine
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    single { Api(get()) }
    single { LocalStore(get()) }
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single { DrinkRepository(get(), get()) }

    single { DiscoveryStateMachine(get()) }
    single { (alias: String) -> DrinkStateMachine(alias, get()) }
    single { SearchStateMachine(get()) }
    single { (request: DrinksRequest) -> ParameterizedDrinkListStateMachine(request, get()) }
    single { DrinkItemStateMachine(get()) }
    single { ShakeToDrinkStateMachine(get(), get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule, platformModule)
}

fun initKoin() = startKoin {
    modules(commonModule, platformModule)
}