package com.popalay.barnee.di

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinkRepositoryImpl
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.drink.DrinkInput
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.favorites.FavoritesStateMachine
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkStateMachine
import com.popalay.barnee.util.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.EMPTY
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType
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
    single {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(get())
                accept(ContentType.Application.Json)
            }
            install(Logging) {
                logger = if (isDebug) Logger.DEFAULT else Logger.EMPTY
                level = LogLevel.ALL
            }
        }
    }
    single<DrinkRepository> { DrinkRepositoryImpl(get(), get()) }

    factory { DiscoveryStateMachine(get()) }
    factory { (input: DrinkInput) -> DrinkStateMachine(input, get()) }
    factory { SearchStateMachine(get()) }
    factory { (input: ParameterizedDrinkListInput) -> ParameterizedDrinkListStateMachine(input, get()) }
    factory { DrinkItemStateMachine(get()) }
    factory { ShakeToDrinkStateMachine(get(), get()) }
    factory { FavoritesStateMachine(get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule, platformModule)
}

fun initKoin() = startKoin {
    modules(commonModule, platformModule)
}
