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

package com.popalay.barnee.domain.log

import com.popalay.barnee.data.Logger
import com.popalay.barnee.domain.core.Action
import com.popalay.barnee.domain.core.Mutation
import com.popalay.barnee.domain.core.SideEffect
import com.popalay.barnee.domain.core.State
import com.popalay.barnee.domain.core.StateMachine

class StateMachineLogger(private val logger: Logger) {
    fun <T : StateMachine<*, *, *, *>> log(tag: T, action: Action) {
        logger.info(tag::class.simpleName.orEmpty(), "action => $action")
    }

    fun <T : StateMachine<*, *, *, *>> log(tag: T, mutation: Mutation) {
        logger.info(tag::class.simpleName.orEmpty(), "mutation => $mutation")
    }

    fun <T : StateMachine<*, *, *, *>> log(tag: T, state: State) {
        logger.info(tag::class.simpleName.orEmpty(), "state => $state")
    }

    fun <T : StateMachine<*, *, *, *>> log(tag: T, sideEffect: SideEffect) {
        logger.info(tag::class.simpleName.orEmpty(), "side effect => $sideEffect")
    }
}
