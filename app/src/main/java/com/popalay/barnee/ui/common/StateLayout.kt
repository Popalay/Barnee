package com.popalay.barnee.ui.common

import androidx.compose.runtime.Composable
import com.popalay.barnee.domain.Fail
import com.popalay.barnee.domain.Loading
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.Success

@JvmName("StateLayoutForList")
@Composable
fun <T : Any> StateLayout(
    value: Result<List<T>>,
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
    value: Result<T>,
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
