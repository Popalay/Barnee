package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.navigationBarsPadding
import com.popalay.barnee.ui.common.PrimarySnackbar
import com.popalay.barnee.ui.screen.drinklist.DrinkItemViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun AddToCollectionScreen() {
    val drinkItemViewModel: DrinkItemViewModel = getViewModel()
    val drinkItemState by drinkItemViewModel.stateFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(drinkItemState.drinkAddedToCollection) {
        drinkItemState.drinkAddedToCollection?.let {
            snackbarHostState.showSnackbar("${it.displayName} was added to favorites")
        }
    }

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