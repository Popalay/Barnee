package com.popalay.barnee.ui.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable

@Composable
fun PrimarySnackbar(snackbarData: SnackbarData) {
    Snackbar(
        snackbarData = snackbarData,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        actionColor = MaterialTheme.colors.secondary
    )
}