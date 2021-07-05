package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.popalay.barnee.domain.addtocollection.AddToCollectionAction
import com.popalay.barnee.domain.addtocollection.AddToCollectionDialogState
import com.popalay.barnee.domain.addtocollection.AddToCollectionSideEffect
import com.popalay.barnee.domain.addtocollection.AddToCollectionState
import com.popalay.barnee.ui.common.PrimarySnackbar
import com.popalay.barnee.ui.util.capitalizeFirstChar
import com.popalay.barnee.util.displayName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun AddToCollectionScreen() {
    AddToCollectionScreen(getViewModel())
}

@Composable
fun AddToCollectionScreen(viewModel: AddToCollectionViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    AddToCollectionScreen(state, viewModel.sideEffectFlow, viewModel::processAction)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddToCollectionScreen(
    state: AddToCollectionState,
    sideEffectFlow: Flow<AddToCollectionSideEffect>,
    onAction: (AddToCollectionAction) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(initialValue = Hidden)
    val scope = rememberCoroutineScope()

    LaunchedEffect(sideEffectFlow, state) {
        scope.launch {
            sideEffectFlow.collectLatest { sideEffect ->
                when (sideEffect) {
                    is AddToCollectionSideEffect.DrinkAddedToFavorites -> {
                        val message = "${sideEffect.drink.displayName.capitalizeFirstChar()} was added to favorites"
                        snackbarHostState.showSnackbar(message, actionLabel = "Change").let { result ->
                            if (result == SnackbarResult.ActionPerformed) {
                                onAction(AddToCollectionAction.ChangeCollectionClicked(sideEffect.drink))
                            }
                        }
                    }
                    is AddToCollectionSideEffect.ShowAddToCollectionDialog -> bottomSheetState.show()
                    is AddToCollectionSideEffect.HideAddToCollectionDialog -> bottomSheetState.hide()
                }
            }
        }
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible) {
            onAction(AddToCollectionAction.AddToCollectionDialogDismissed)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetElevation = 1.dp,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContentColor = MaterialTheme.colors.onBackground,
        scrimColor = MaterialTheme.colors.background.copy(alpha = 0.7F),
        sheetContent = {
            Crossfade(state.dialogState) { dialogState ->
                when (dialogState) {
                    is AddToCollectionDialogState.ChooseCollectionFor -> ChooseCollectionBottomSheet(
                        onCollectionClicked = { onAction(AddToCollectionAction.CollectionClicked(it, dialogState.drink)) },
                        onCreateNewClicked = { onAction(AddToCollectionAction.CreateCollectionClicked(dialogState.drink)) }
                    )
                    is AddToCollectionDialogState.CreateCollectionFor -> CreateCollectionBottomSheet(
                        drink = dialogState.drink,
                        collectionName = state.newCollectionName,
                        canBeSaved = state.isNewCollectionValid,
                        onSaveClicked = { onAction(AddToCollectionAction.SaveCollectionClicked(dialogState.drink)) },
                        onBackClicked = { onAction(AddToCollectionAction.BackFromCollectionCreationClicked(dialogState.drink)) },
                        onCollectionNameChanged = { onAction(AddToCollectionAction.NewCollectionNameChanged(it)) }
                    )
                    AddToCollectionDialogState.Empty -> Spacer(modifier = Modifier.height(1.dp))
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { PrimarySnackbar(it) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}
