/*
 * Copyright (c) 2021 Denys Nykyforov
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

import com.popalay.barnee.ui.screen.addtocollection.AddToCollectionViewModel
import com.popalay.barnee.ui.screen.app.AppViewModel
import com.popalay.barnee.ui.screen.collection.CollectionViewModel
import com.popalay.barnee.ui.screen.collectionlist.CollectionListViewModel
import com.popalay.barnee.ui.screen.discovery.DiscoveryViewModel
import com.popalay.barnee.ui.screen.drink.DrinkViewModel
import com.popalay.barnee.ui.screen.drinklist.DrinkItemViewModel
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListViewModel
import com.popalay.barnee.ui.screen.search.SearchViewModel
import com.popalay.barnee.ui.screen.shaketodrink.ShakeToDrinkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel { AppViewModel(get { it }) }
    viewModel { DiscoveryViewModel(get { it }) }
    viewModel { DrinkViewModel(get { it }) }
    viewModel { SearchViewModel(get { it }) }
    viewModel { ParameterizedDrinkListViewModel(get { it }) }
    viewModel { DrinkItemViewModel(get { it }) }
    viewModel { ShakeToDrinkViewModel(get { it }) }
    viewModel { CollectionViewModel(get { it }) }
    viewModel { CollectionListViewModel(get { it }) }
    viewModel { AddToCollectionViewModel(get { it }) }
}
