package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.popalay.barnee.R.drawable
import com.popalay.barnee.ui.screen.navigation.LocalNavController

@Composable
fun BackButton(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(drawable.ic_arrow_back),
            contentDescription = "Back",
            modifier = Modifier.size(32.dp)
        )
    }
}