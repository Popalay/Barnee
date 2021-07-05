package com.popalay.barnee.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.popalay.barnee.domain.SideEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T : R, R> StateFlow<T>.collectAsStateWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val flowLifecycleAware = remember(this, lifecycleOwner) {
        flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
    }
    return flowLifecycleAware.collectAsState(value, context)
}

@Composable
fun <T : SideEffect> LifecycleAwareSideEffect(
    sideEffectFlow: Flow<T>,
    key2: Any? = null,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val sideEffectFlowLifecycleAware = remember(sideEffectFlow, lifecycleOwner) {
        sideEffectFlow.flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(sideEffectFlowLifecycleAware, key2) {
        scope.launch {
            sideEffectFlowLifecycleAware.collectLatest { action(it) }
        }
    }
}
