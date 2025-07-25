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

package com.popalay.barnee.ui.common

import androidx.compose.runtime.Composable
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.LazyPagingItems
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
        value is Fail                              -> errorState()
        value.isEmpty                              -> emptyState()
        value is Success                           -> content(value())
    }
}

@Suppress("USELESS_IS_CHECK")
@Composable
fun <T : Any> StateLayout(
    value: LazyPagingItems<T>,
    loadingState: @Composable () -> Unit = {},
    emptyState: @Composable () -> Unit = {},
    errorState: @Composable () -> Unit = {},
    content: @Composable (value: LazyPagingItems<T>) -> Unit
) {
    when {
        value.isUninitialized || value.loadState.refresh == LoadStateLoading -> loadingState()
        value.loadState.refresh is LoadStateError                            -> errorState()
        value.itemCount == 0                                                 -> emptyState()
        else                                                                 -> content(value)
    }
}
