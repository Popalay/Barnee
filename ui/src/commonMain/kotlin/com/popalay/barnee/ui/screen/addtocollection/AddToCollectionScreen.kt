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

package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moriatsushi.insetsx.ExperimentalSoftwareKeyboardApi
import com.moriatsushi.insetsx.ime
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.addtocollection.AddToCollectionAction
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState
import com.popalay.barnee.domain.addtocollection.AddToCollectionState
import com.popalay.barnee.domain.addtocollection.AddToCollectionStateMachine
import com.popalay.barnee.domain.collectionlist.CollectionListState
import com.popalay.barnee.domain.collectionlist.CollectionListStateMachine
import com.popalay.barnee.domain.navigation.ScreenWithInputAsKey
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.platform.collectAsStateWithLifecycle
import com.popalay.barnee.util.asStateFlow
import io.matthewnelson.component.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class AddToCollectionScreen(override val input: DrinkMinimumData) : ScreenWithInputAsKey<DrinkMinimumData> {

    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<AddToCollectionStateMachine>(parameters = { parametersOf(input) })
        val collectionListStateMachine = injectStateMachine<CollectionListStateMachine>()
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()
        val collectionListState by collectionListStateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        AddToCollectionScreen(state, collectionListState, stateMachine::dispatch)
    }
}

@OptIn(ExperimentalSoftwareKeyboardApi::class)
@Composable
private fun AddToCollectionScreen(
    state: AddToCollectionState,
    collectionListState: CollectionListState,
    onAction: (Action) -> Unit
) {
    Spacer(Modifier.height(1.dp))
    Crossfade(state.dialogState, label = "add-to-collection-dialog") { dialogState ->
        when (dialogState) {
            is AddToCollectionDialogState.ChooseCollection -> ChooseCollectionBottomSheet(
                state = collectionListState,
                onCollectionClicked = { onAction(AddToCollectionAction.CollectionClicked(it)) },
                onCreateNewClicked = { onAction(AddToCollectionAction.CreateCollectionClicked) },
                bottomPadding = WindowInsets.ime.asPaddingValues()
            )

            is AddToCollectionDialogState.CreateCollection -> CreateCollectionBottomSheet(
                drink = state.drink,
                collectionName = state.newCollectionName,
                canBeSaved = state.isNewCollectionValid,
                onSaveClicked = { onAction(AddToCollectionAction.SaveCollectionClicked) },
                onBackClicked = { onAction(AddToCollectionAction.BackFromCollectionCreationClicked) },
                onCollectionNameChanged = { onAction(AddToCollectionAction.NewCollectionNameChanged(it)) },
                bottomPadding = WindowInsets.ime.asPaddingValues()
            )
        }
    }
}
