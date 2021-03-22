package com.popalay.barnee.ui.common

import androidx.compose.runtime.Composable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success

@JvmName("StateLayoutForList")
@Composable
fun <T : Any> StateLayout(
    value: Async<List<T>>,
    loadingState: @Composable () -> Unit = {},
    emptyState: @Composable () -> Unit = {},
    errorState: @Composable () -> Unit = {},
    content: @Composable (value: List<T>) -> Unit
) {
    when {
        value is Loading -> loadingState()
        value is Fail -> errorState()
        value()?.isEmpty() == true -> emptyState()
        value is Success -> content(value())
    }
}

@Composable
fun <T : Any> StateLayout(
    value: Async<T>,
    loadingState: @Composable () -> Unit = {},
    emptyState: @Composable () -> Unit = {},
    errorState: @Composable () -> Unit = {},
    content: @Composable (value: T) -> Unit
) {
    when {
        value is Loading -> loadingState()
        value is Fail -> errorState()
        value() == null -> emptyState()
        value is Success -> content(value())
    }
}
