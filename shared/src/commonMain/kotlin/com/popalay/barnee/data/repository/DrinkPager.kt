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

data class PageRequest(
    val skip: Int,
    val take: Int
)

class DrinkPager(private val request: suspend (PageRequest) -> List<Drink>) {
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
                nextKey = { skip + take }
            )
        }
    )
}