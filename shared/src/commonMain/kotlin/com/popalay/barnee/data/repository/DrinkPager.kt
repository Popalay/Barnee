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

package com.popalay.barnee.data.repository

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import com.popalay.barnee.data.model.Drink
import io.ktor.client.plugins.ClientRequestException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.Flow

internal data class PageRequest(
    val skip: Int,
    val take: Int
)

internal class DrinkPager(private val request: suspend (PageRequest) -> List<Drink>) {
    val pages
        get(): Flow<PagingData<Drink>> = createPager().flow

    private fun createPager() = Pager(
        config = DefaultPagingConfig,
        initialKey = 0,
        pagingSourceFactory = { DrinkPagingSource(request) }
    )

    companion object {
        private const val DefaultPageSize = 60

        private val DefaultPagingConfig = PagingConfig(
            pageSize = DefaultPageSize,
            initialLoadSize = DefaultPageSize * 2,
            enablePlaceholders = false
        )
    }
}

private class DrinkPagingSource(
    private val request: suspend (PageRequest) -> List<Drink>
) : PagingSource<Int, Drink>() {

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Drink> {
        val position = params.key ?: 0
        val pageSize = params.loadSize
        return try {
            val pageRequest = PageRequest(position, pageSize)
            val items = request(pageRequest)

            val nextKey = if (items.isEmpty()) {
                null
            } else {
                position + pageSize
            }
            PagingSourceLoadResultPage(
                data = items,
                prevKey = if (position == 0) null else position - pageSize,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            PagingSourceLoadResultError<Int, Drink>(exception)
        } catch (exception: ClientRequestException) {
            PagingSourceLoadResultError<Int, Drink>(exception)
        } as PagingSourceLoadResult<Int, Drink>
    }

    override fun getRefreshKey(state: PagingState<Int, Drink>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
