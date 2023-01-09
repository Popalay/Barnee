/*
 * Copyright (c) 2023 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.di

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.local.LocalStoreImpl
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.data.repository.CollectionRepositoryImpl
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinkRepositoryImpl
import com.popalay.barnee.data.repository.ShareRepository
import com.popalay.barnee.data.repository.ShareRepositoryImpl
import com.popalay.barnee.domain.addtocollection.AddToCollectionStateMachine
import com.popalay.barnee.domain.collection.CollectionInput
import com.popalay.barnee.domain.collection.CollectionStateMachine
import com.popalay.barnee.domain.collectionlist.CollectionListStateMachine
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.drink.DrinkInput
import com.popalay.barnee.domain.drink.DrinkStateMachine
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.log.NavigationLogger
import com.popalay.barnee.domain.log.StateMachineLogger
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.RouterImpl
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.domain.shakedrink.ShakeToDrinkStateMachine
import com.popalay.barnee.domain.usecase.GetCollectionUseCase
import com.popalay.barnee.util.EmptyLogger
import com.popalay.barnee.util.RealLogger
import com.popalay.barnee.util.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    single { if (isDebug) RealLogger() else EmptyLogger }
    single { StateMachineLogger(get()) }
    single { NavigationLogger(get()) }

    single { Api(get()) }
    single<LocalStore> { LocalStoreImpl(get()) }
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                logger = if (isDebug) Logger.DEFAULT else Logger.EMPTY
                level = LogLevel.ALL
            }
        }
    }
    single<DrinkRepository> { DrinkRepositoryImpl(get(), get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get(), get(), get()) }
    single<ShareRepository> { ShareRepositoryImpl(get(), get()) }
    single<Router> { RouterImpl(get()) }

    single { GetCollectionUseCase(get(), get()) }

    factory { DiscoveryStateMachine(get(), get()) }
    factory { (input: DrinkInput) -> DrinkStateMachine(input, get(), get(), get()) }
    factory { SearchStateMachine(get()) }
    factory { (input: ParameterizedDrinkListInput) -> ParameterizedDrinkListStateMachine(input, get()) }
    factory { DrinkItemStateMachine(get(), get()) }
    factory { ShakeToDrinkStateMachine(get(), get()) }
    factory { (input: CollectionInput) -> CollectionStateMachine(input, get(), get(), get(), get()) }
    factory { CollectionListStateMachine(get(), get()) }
    factory { AddToCollectionStateMachine(get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule, platformModule)
}

fun initKoin() = startKoin {
    modules(commonModule, platformModule)
}
