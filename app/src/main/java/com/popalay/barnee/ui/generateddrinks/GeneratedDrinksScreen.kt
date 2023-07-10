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

package com.popalay.barnee.ui.generateddrinks

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.popalay.barnee.di.injectStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateToAction
import com.popalay.barnee.domain.navigation.ScreenWithInputAsKey
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
import com.popalay.barnee.ui.screen.bartender.ShakeCocktailButton
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListScreen
import com.popalay.barnee.util.asStateFlow
import org.koin.core.parameter.parametersOf

data class GeneratedDrinksScreen(
    override val input: ParameterizedDrinkListInput
) : ScreenWithInputAsKey<ParameterizedDrinkListInput> {

    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<ParameterizedDrinkListStateMachine>(parameters = { parametersOf(input) })
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        ParameterizedDrinkListScreen(state, stateMachine::dispatch) {
            ShakeCocktailButton(
                onShakeClick = { stateMachine.dispatch(NavigateToAction(AppScreens.Bartender)) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
