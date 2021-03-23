package com.popalay.barnee.data.repository

import com.futuremind.koru.ExportedScopeProvider
import com.futuremind.koru.ScopeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@ExportedScopeProvider
class MainScopeProvider: ScopeProvider {
    override val scope: CoroutineScope = MainScope()
}