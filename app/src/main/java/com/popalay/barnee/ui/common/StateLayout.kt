package com.popalay.barnee.ui.common

import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.popalay.barnee.domain.Fail
import com.popalay.barnee.domain.Loading
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.Success
import com.popalay.barnee.domain.Uninitialized
import com.popalay.barnee.domain.isEmpty

@Composable
fun <T : Any> StateLayout(
    value: Result<T>,
    loadingState: @Composable () -> Unit = {},
    emptyState: @Composable () -> Unit = {},
    errorState: @Composable () -> Unit = {},
    content: @Composable (value: T) -> Unit
) {
    when {
        value is Uninitialized || value is Loading -> loadingState()
        value is Fail -> errorState()
        value.isEmpty -> emptyState()
        value is Success -> content(value())
    }
}

@Composable
fun <T : Any> StateLayout(
    value: LazyPagingItems<T>,
    loadingState: @Composable () -> Unit = {},
    emptyState: @Composable () -> Unit = {},
    errorState: @Composable () -> Unit = {},
    content: @Composable (value: LazyPagingItems<T>) -> Unit
) {
    when {
        value.isUninitialized || value.loadState.refresh == LoadState.Loading -> loadingState()
        value.loadState.refresh is LoadState.Error -> errorState()
        value.itemCount == 0 -> emptyState()
        else -> content(value)
    }
}