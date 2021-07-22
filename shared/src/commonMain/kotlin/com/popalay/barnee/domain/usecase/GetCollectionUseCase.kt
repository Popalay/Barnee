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

package com.popalay.barnee.domain.usecase

import com.kuuurt.paging.multiplatform.PagingData
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.repository.CollectionRepository
import com.popalay.barnee.data.repository.DrinkRepository
import com.popalay.barnee.data.repository.DrinksRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCollectionUseCase(
    private val collectionRepository: CollectionRepository,
    private val drinkRepository: DrinkRepository
) {
    data class Input(
        val name: String,
        val aliases: Set<String> = emptySet()
    )

    data class Output(
        val pagingDataFlow: Flow<PagingData<Drink>>,
        val collection: Collection?,
        val isCollectionExists: Boolean
    )

    operator fun invoke(input: Input): Flow<Output> = collectionRepository.collections()
        .map { collections -> collections.firstOrNull { it.name == input.name } }
        .map { collection ->
            val isCollectionExists = collection?.aliases?.containsAll(input.aliases) ?: false
            val request = if (isCollectionExists) DrinksRequest.Collection(input.name) else DrinksRequest.ByAliases(input.aliases)
            val flow = drinkRepository.drinks(request)

            Output(flow, collection, isCollectionExists)
        }
}
