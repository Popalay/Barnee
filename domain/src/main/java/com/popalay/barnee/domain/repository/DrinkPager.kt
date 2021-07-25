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

package com.popalay.barnee.data.repository

import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.popalay.barnee.data.model.Drink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.plus

private const val DEFAULT_PAGE_SIZE = 60

private val DefaultPagingConfig = PagingConfig(
    pageSize = DEFAULT_PAGE_SIZE,
    initialLoadSize = DEFAULT_PAGE_SIZE * 2
)

internal data class PageRequest(
    val skip: Int,
    val take: Int
)

internal class DrinkPager(private val request: suspend (PageRequest) -> List<Drink>) {
    val pages
        get(): Flow<PagingData<Drink>> {
            val scope = MainScope() + Job()
            return createPager(scope)
                .pagingData
                .cachedIn(scope)
        }

    private fun createPager(coroutineScope: CoroutineScope) = Pager(
        config = DefaultPagingConfig,
        clientScope = coroutineScope,
        initialKey = 0,
        getItems = { skip, take ->
            val pageRequest = PageRequest(skip, take)
            val items = request(pageRequest)
            PagingResult(
                items = items,
                currentKey = skip,
                prevKey = { (skip - take).coerceAtLeast(0) },
                nextKey = { if (items.size == take) skip + take else null }
            )
        }
    )
}
