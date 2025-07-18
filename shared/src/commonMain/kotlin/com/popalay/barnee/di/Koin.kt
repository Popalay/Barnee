/*
 * Copyright (c) 2025 Denys Nykyforov
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

import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.local.LocalStoreImpl
import com.popalay.barnee.data.message.MessagesProvider
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.data.remote.AiApi
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.remote.CloudinaryApi
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.data.repository.CollectionRepositoryImpl
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinkRepositoryImpl
import com.popalay.barnee.data.repository.ShareRepository
import com.popalay.barnee.data.repository.ShareRepositoryImpl
import com.popalay.barnee.domain.addtocollection.AddToCollectionStateMachine
import com.popalay.barnee.domain.bartender.BartenderStateMachine
import com.popalay.barnee.domain.collection.CollectionInput
import com.popalay.barnee.domain.collection.CollectionStateMachine
import com.popalay.barnee.domain.collectionlist.CollectionListStateMachine
import com.popalay.barnee.domain.deeplink.CollectionDeeplinkHandler
import com.popalay.barnee.domain.deeplink.DeeplinkHandler
import com.popalay.barnee.domain.deeplink.DeeplinkManager
import com.popalay.barnee.domain.deeplink.DrinkDeeplinkHandler
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
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
import com.popalay.barnee.shared.BuildKonfig
import com.popalay.barnee.util.isDebug
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import com.aallam.openai.api.logging.LogLevel as OpenAiLogLevel
import com.aallam.openai.api.logging.Logger as OpenAiLogger

val commonModule = module {
    single { StateMachineLogger() }

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
                logger = if (isDebug) Logger.SIMPLE else Logger.EMPTY
                level = LogLevel.ALL
            }
        }
    }
    single<DrinkRepository> { DrinkRepositoryImpl(get(), get(), get(), get(), get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get(), get(), get()) }
    single<ShareRepository> { ShareRepositoryImpl(get(), get()) }
    single<OpenAI> {
        OpenAI(
            token = BuildKonfig.OPEN_AI_API_KEY,
            logging = LoggingConfig(
                logLevel = OpenAiLogLevel.Body,
                logger = if (isDebug) OpenAiLogger.Simple else OpenAiLogger.Empty
            )
        )
    }
    single { CloudinaryApi(get(), BuildKonfig.CLOUDINARY_API_SECRET) }
    single { AiApi(get(), get(), get()) }
    single { MessagesProvider() }
    single { NavigationLogger() }
    single<Router> { RouterImpl(get()) }
    single<Set<DeeplinkHandler>> { setOf(DrinkDeeplinkHandler(), CollectionDeeplinkHandler()) }
    single { DeeplinkManager(get(), get()) }

    single { GetCollectionUseCase(get(), get()) }

    factory { DiscoveryStateMachine(get()) }
    factory { (input: DrinkMinimumData) -> DrinkStateMachine(input, get(), get(), get(), get()) }
    factory { SearchStateMachine(get()) }
    factory { (input: ParameterizedDrinkListInput) -> ParameterizedDrinkListStateMachine(input, get()) }
    factory { DrinkItemStateMachine(get(), get()) }
    factory { ShakeToDrinkStateMachine(get(), get()) }
    factory { (input: CollectionInput) -> CollectionStateMachine(input, get(), get(), get()) }
    factory { CollectionListStateMachine(get()) }
    factory { (input: DrinkMinimumData) -> AddToCollectionStateMachine(input, get()) }
    factory { BartenderStateMachine(get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule, platformModule)
}

fun initKoin() = startKoin {
    modules(commonModule, platformModule)
}
