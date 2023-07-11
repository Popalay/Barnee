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

package com.popalay.barnee.domain.navigation

import com.popalay.barnee.domain.log.NavigationLogger
import com.popalay.barnee.util.CFlow
import com.popalay.barnee.util.wrap
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface StackChange {
    data class Push(val destinations: List<TypedScreenProvider>) : StackChange {
        constructor(destination: TypedScreenProvider) : this(listOf(destination))
    }

    data class Replace(val destination: TypedScreenProvider) : StackChange

    data class ReplaceAll(val destinations: List<TypedScreenProvider>) : StackChange {
        constructor(destination: TypedScreenProvider) : this(listOf(destination))
    }

    object Pop : StackChange
}

interface Router {
    val stackChangeFlow: CFlow<StackChange>
    suspend fun updateStack(stackChange: StackChange)
}

internal class RouterImpl(
    private val navigationLogger: NavigationLogger
) : Router {
    private val _routeFlow = MutableSharedFlow<StackChange>()
    override val stackChangeFlow: CFlow<StackChange> = _routeFlow.asSharedFlow().wrap()

    override suspend fun updateStack(stackChange: StackChange) {
        navigationLogger.log(this, stackChange)
        _routeFlow.emit(stackChange)
    }
}
