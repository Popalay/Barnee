package com.popalay.barnee.data.local

import kotlinx.coroutines.flow.Flow

actual object LocalStore {
    actual fun getFavoriteDrinks(): Flow<Set<String>> {
        throw NotImplementedError("Feature is not implemented")
    }

    actual suspend fun saveFavorite(alias: String) {
        throw NotImplementedError("Feature is not implemented")
    }

    actual suspend fun removeFavorite(alias: String) {
        throw NotImplementedError("Feature is not implemented")
    }
}
