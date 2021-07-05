package com.popalay.barnee.ui.screen.addtocollection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun BottomSheetContent(
    title: @Composable () -> Unit,
    action: @Composable () -> Unit,
    body: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigation: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.padding(bottom = 24.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            navigation?.let {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    ) {
                        navigation()
                    }
                }
            }
            ProvideTextStyle(MaterialTheme.typography.subtitle1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 16.dp),
                ) {
                    title()
                }
            }
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    action()
                }
            }
        }
        Divider(modifier = Modifier.padding(bottom = 24.dp))
        body()
        Spacer(modifier = Modifier.navigationBarsWithImePadding())
    }
}
