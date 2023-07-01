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

package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.popalay.barnee.domain.addtocollection.AddToCollectionAction
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState
import com.popalay.barnee.domain.addtocollection.AddToCollectionInput
import com.popalay.barnee.domain.addtocollection.AddToCollectionState
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddToCollectionScreen(input: AddToCollectionInput) {
    AddToCollectionScreen(getViewModel { parametersOf(input) })
}

@Composable
fun AddToCollectionScreen(viewModel: AddToCollectionViewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    AddToCollectionScreen(state, viewModel::dispatchAction)
}

@Composable
fun AddToCollectionScreen(
    state: AddToCollectionState,
    onAction: (AddToCollectionAction) -> Unit
) {
    Crossfade(state.dialogState, label = "add-to-collection-dialog") { dialogState ->
        when (dialogState) {
            is AddToCollectionDialogState.ChooseCollection -> ChooseCollectionBottomSheet(
                onCollectionClicked = { onAction(AddToCollectionAction.CollectionClicked(it)) },
                onCreateNewClicked = { onAction(AddToCollectionAction.CreateCollectionClicked) }
            )

            is AddToCollectionDialogState.CreateCollection -> CreateCollectionBottomSheet(
                drink = state.drink,
                collectionName = state.newCollectionName,
                canBeSaved = state.isNewCollectionValid,
                onSaveClicked = { onAction(AddToCollectionAction.SaveCollectionClicked) },
                onBackClicked = { onAction(AddToCollectionAction.BackFromCollectionCreationClicked) },
                onCollectionNameChanged = { onAction(AddToCollectionAction.NewCollectionNameChanged(it)) }
            )
        }
    }
}
